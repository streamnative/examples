#include <librdkafka/rdkafka.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

/* Typical include path is <libserdes/serdes.h> */
extern "C" {
#include <libserdes/serdes-avro.h>
}

static int run = 1;
static int exit_eof = 0;
static int verbosity = 2;

#define FATAL(reason...)               \
  do {                                 \
    fprintf(stderr, "FATAL: " reason); \
    exit(1);                           \
  } while (0)

/**
 * Parse, deserialize and print a consumed message.
 */
static void parse_msg(rd_kafka_message_t *rkmessage, serdes_t *serdes) {
  avro_value_t avro;
  serdes_err_t err;
  serdes_schema_t *schema;
  char errstr[512];
  char *as_json;

  /* Automatic deserialization using message framing */
  err = serdes_deserialize_avro(serdes, &avro, &schema, rkmessage->payload,
                                rkmessage->len, errstr, sizeof(errstr));
  if (err) {
    fprintf(stderr, "%% serdes_deserialize_avro failed: %s\n", errstr);
    return;
  }

  if (verbosity > 1)
    fprintf(stderr,
            "%% Successful Avro deserialization using "
            "schema %s id %d\n",
            serdes_schema_name(schema), serdes_schema_id(schema));

  /* Convert to JSON and print */
  if (avro_value_to_json(&avro, 1, &as_json))
    fprintf(stderr, "%% avro_to_json failed: %s\n", avro_strerror());
  else {
    printf("%s\n", as_json);
    free(as_json);
  }

  avro_value_decref(&avro);
}

static void run_consumer(rd_kafka_conf_t *rk_conf,
                         rd_kafka_topic_conf_t *rkt_conf, const char *topic,
                         int32_t partition, serdes_t *serdes) {
  rd_kafka_t *rk;
  rd_kafka_topic_t *rkt;
  char errstr[512];
  rd_kafka_message_t *rkmessage;

  rk = rd_kafka_new(RD_KAFKA_CONSUMER, rk_conf, errstr, sizeof(errstr));
  if (!rk) FATAL("Failed to create consumer: %s\n", errstr);

  rkt = rd_kafka_topic_new(rk, topic, rkt_conf);

  rd_kafka_consume_start(rkt, partition, RD_KAFKA_OFFSET_BEGINNING);

  while (run) {
    rkmessage = rd_kafka_consume(rkt, partition, 500);
    if (!rkmessage) continue;

    if (rkmessage->err) {
      if (rkmessage->err == RD_KAFKA_RESP_ERR__PARTITION_EOF) {
        if (exit_eof) run = 0;
      } else {
        printf("Consumed message (offset %" PRId64
               ") "
               "error: %s\n",
               rkmessage->offset, rd_kafka_message_errstr(rkmessage));
      }

    } else {
      parse_msg(rkmessage, serdes);
    }

    rd_kafka_message_destroy(rkmessage);
  }

  rd_kafka_consume_stop(rkt, partition);

  run = 1;
  while (run && rd_kafka_outq_len(rk) > 0) usleep(100 * 1000);

  rd_kafka_topic_destroy(rkt);
  rd_kafka_destroy(rk);
}

static void run_producer(rd_kafka_conf_t *rk_conf,
                         rd_kafka_topic_conf_t *rkt_conf, const char *topic,
                         int32_t partition, const char *schema_name,
                         int schema_id, const char *schema_def,
                         serdes_t *serdes) {
  rd_kafka_t *rk;
  rd_kafka_topic_t *rkt;
  char errstr[512];
  char buf[1024];
  serdes_schema_t *schema = NULL;

  if (schema_def) {
    schema = serdes_schema_add(serdes, schema_name, schema_id, schema_def, -1,
                               errstr, sizeof(errstr));
    if (!schema) FATAL("Failed to register schema: %s\n", errstr);

    if (verbosity >= 1)
      fprintf(stderr, "%% Added schema %s with id %d\n",
              serdes_schema_name(schema), serdes_schema_id(schema));

  } else {
    schema = serdes_schema_get(serdes, schema_name, schema_id, errstr,
                               sizeof(errstr));
    if (!schema)
      FATAL("Failed to acquire schema \"%s\": %s\n", schema_name, errstr);

    if (verbosity >= 1)
      printf("%% Using schema %s with id %d\n", serdes_schema_name(schema),
             serdes_schema_id(schema));
  }

  rk = rd_kafka_new(RD_KAFKA_PRODUCER, rk_conf, errstr, sizeof(errstr));
  if (!rk) FATAL("%% Failed to create producer: %s\n", errstr);

  rkt = rd_kafka_topic_new(rk, topic, rkt_conf);

  fprintf(stderr,
          "%% Use \"schema: <json>\" to specify a new schema\n"
          "%% Use \"str: <string>\" to produce an Avro-encoded string\n"
          "%% Ctrl-D to exit\n");

  /* FIXME: JSON-to-Avro conversion */

  while (run && fgets(buf, sizeof(buf) - 1, stdin)) {
    char *t;

    if ((t = strchr(buf, '\n'))) *t = '\0';

    if (!strncmp(buf, "schema: ", 8)) {
      /* New schema definition */
      schema = serdes_schema_add(serdes, schema_name, -1, buf + 8, -1, errstr,
                                 sizeof(errstr));
      if (!schema) {
        printf("%% Failed to register schema: %s\n", errstr);
        continue;
      }

      if (verbosity >= 1)
        fprintf(stderr, "%% Added schema %s with id %d\n",
                serdes_schema_name(schema), serdes_schema_id(schema));
      continue;

    } else if (!strncmp(buf, "str: ", 5)) {
      /* Emit a single Avro string */
      avro_value_t val;
      void *ser_buf = NULL;
      size_t ser_buf_size;

      avro_generic_string_new(&val, buf + 5);

      if (serdes_schema_serialize_avro(schema, &val, &ser_buf, &ser_buf_size,
                                       errstr, sizeof(errstr))) {
        fprintf(stderr, "%% serialize_avro() failed: %s\n", errstr);
        continue;
      }

      if (rd_kafka_produce(rkt, partition, RD_KAFKA_MSG_F_FREE, ser_buf,
                           ser_buf_size, NULL, 0, NULL) == -1) {
        fprintf(stderr, "%% Failed to produce message: %s\n",
                rd_kafka_err2str(rd_kafka_last_error()));
        free(ser_buf);
      } else {
        if (verbosity >= 3)
          fprintf(stderr, "%% Produced %zd bytes\n", ser_buf_size);
      }

      avro_value_decref(&val);
    }
  }

  run = 1;
  while (run && rd_kafka_outq_len(rk) > 0) usleep(100 * 1000);

  rd_kafka_topic_destroy(rkt);
  rd_kafka_destroy(rk);
}

static void usage(const char *argv0) {
  fprintf(stderr,
          "Usage: %s -C|-P <options>\n"
          "\n"
          "Options:\n"
          " -C                Consumer mode\n"
          " -P                Producer mode\n"
          " -Q                Query schema registry\n"
          " -b <brokers..>    Kafka broker(s)\n"
          " -t <topic>        Kafka topic\n"
          " -p <partition>    Kafka partition\n"
          " -r <schreg-urls>  Schema registry URL\n"
          " -s <schema-name>  Schema/subject name\n"
          " -S <schema-def>   Schema definition (JSON)\n"
          " -X serdes.<n>=<v> Set Serdes configuration\n"
          " -X <n>=<v>        Set Kafka configuration\n"
          " -v                Increase verbosity\n"
          " -q                Decrease verbosity\n"
          "\n"
          "Examples:\n"
          "\n"
          " Run consumer, deserialize and print Avro messages as JSON:\n"
          "  %s -C -b <broker> -t <topic> -p <partition> -r <schema-reg-urls>\n"
          "\n"
          " Run producer, serialize Avro messages and produce to topic,\n"
          " using supplied schema definition that will be registered\n"
          " to schema-registry:\n"
          "  %s -P -b <broker> -t <topic> -p <partition> "
          "-r <schema-reg-urls> -s <schema-definition>\n"
          "\n"
          " Run producer, .. but use an existing schema based on "
          "schema id:\n"
          "  %s -P -b <broker> -t <topic> -p <partition> "
          "-r <schema-reg-urls> -s <schema-id>\n"
          "\n"
          " Add schema to schema registry:\n"
          " %s -Q -r <schema-reg-urls> "
          "-s <schema/subject-name> -S <schema-definition>\n"
          "\n"
          " Get schema from schema registry based on name or id:\n"
          " %s -Q -r <schema-reg-urls> -s <schema-id|name>\n"
          "\n",
          argv0, argv0, argv0, argv0, argv0, argv0);
  exit(1);
}

static void sig_term(int sig) {
  run = 0;
  fclose(stdin);
}

int main(int argc, char **argv) {
  const char *topic = "test";
  int partition = 0;
  char mode = 0;
  rd_kafka_conf_t *rk_conf;
  rd_kafka_topic_conf_t *rkt_conf;
  serdes_conf_t *sconf;
  serdes_t *serdes;
  serdes_err_t err;
  char errstr[512];
  int opt;
  int schema_id = -1;
  const char *schema_name = NULL;
  const char *schema_def = NULL;

  signal(SIGINT, sig_term);
  signal(SIGTERM, sig_term);

  rk_conf = rd_kafka_conf_new();
  rkt_conf = rd_kafka_topic_conf_new();

  sconf = serdes_conf_new(NULL, 0,
                          /* Default URL */
                          "schema.registry.url", "http://localhost:8081", NULL);

  while ((opt = getopt(argc, argv, "CPQb:t:p:r:s:S:X:vq")) != -1) {
    switch (opt) {
      case 'C':
      case 'P':
      case 'Q':
        mode = (char)opt;
        break;

      case 'b':
        if (rd_kafka_conf_set(rk_conf, "metadata.broker.list", optarg, errstr,
                              sizeof(errstr)) != RD_KAFKA_CONF_OK)
          FATAL("%s\n", errstr);
        break;

      case 't':
        topic = optarg;
        break;

      case 'p':
        partition = atoi(optarg);
        break;

      case 'r':
        if (serdes_conf_set(sconf, "schema.registry.url", optarg, errstr,
                            sizeof(errstr)) != SERDES_ERR_OK)
          FATAL("%s\n", errstr);
        break;

      case 's':
        schema_name = optarg;
        break;

      case 'S':
        schema_def = optarg;
        break;

      case 'X': {
        char *name, *val;
        rd_kafka_conf_res_t res;

        name = optarg;
        if (!(val = strchr(name, '=')))
          FATAL(
              "Expected "
              "-X property=value, not %s\n",
              name);

        *val = '\0';
        val++;

        err = serdes_conf_set(sconf, name, val, errstr, sizeof(errstr));
        if (err == SERDES_ERR_OK)
          break;
        else if (err != SERDES_ERR_CONF_UNKNOWN)
          FATAL("%s\n", errstr);
        else
          /*FALLTHRU for CONF_UNKNOWN*/;

        res = RD_KAFKA_CONF_UNKNOWN;
        /* Try "topic." prefixed properties on topic
         * conf first, and then fall through to global if
         * it didnt match a topic configuration property. */
        if (!strncmp(name, "topic.", strlen("topic.")))
          res = rd_kafka_topic_conf_set(rkt_conf, name + strlen("topic."), val,
                                        errstr, sizeof(errstr));

        if (res == RD_KAFKA_CONF_UNKNOWN)
          res = rd_kafka_conf_set(rk_conf, name, val, errstr, sizeof(errstr));

        if (res != RD_KAFKA_CONF_OK) FATAL("%s\n", errstr);
      } break;

      case 'v':
        verbosity++;
        break;
      case 'q':
        verbosity--;
        break;

      default:
        fprintf(stderr, "%% Unknown option -%c\n", opt);
        usage(argv[0]);
    }
  }

  if (!mode) usage(argv[0]);

  serdes = serdes_new(sconf, errstr, sizeof(errstr));
  if (!serdes) {
    fprintf(stderr, "%% Failed to create serdes handle: %s\n", errstr);
    exit(1);
  }

  if (schema_name) {
    if (strspn(schema_name, "0123456789") == strlen(schema_name)) {
      schema_id = atoi(schema_name);
      schema_name = NULL;
    }
  }

  if (mode == 'C') /* Consumer */
    run_consumer(rk_conf, rkt_conf, topic, partition, serdes);
  else if (mode == 'P') /* Producer */
    run_producer(rk_conf, rkt_conf, topic, partition, schema_name, schema_id,
                 schema_def, serdes);
  else if (mode == 'Q') { /* Schema registry query */
    serdes_schema_t *schema;

    if (!schema_name && schema_id == -1)
      FATAL("Expected schema -s <id> or -s <name>\n");

    schema = serdes_schema_get(serdes, schema_name, schema_id, errstr,
                               sizeof(errstr));
    if (!schema) FATAL("%s\n", errstr);

    printf("Schema \"%s\" id %d: %s\n", serdes_schema_name(schema),
           serdes_schema_id(schema), serdes_schema_definition(schema));
  }

  serdes_destroy(serdes);

  rd_kafka_wait_destroyed(5000);

  return 0;
}
