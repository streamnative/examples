package main

import (
	"context"
	"crypto/tls"
	"encoding/json"
	"flag"
	"fmt"
	"os"
	"os/signal"
	"strings"
	"sync"

	"github.com/twmb/franz-go/pkg/kadm"
	"github.com/twmb/franz-go/pkg/kgo"
	"github.com/twmb/franz-go/pkg/sasl/plain"
	"github.com/twmb/tlscfg"

	log "github.com/sirupsen/logrus"
)

type SASLConfig struct {
	Mechanism string
	Username  string
	Password  string
}

type TLSConfig struct {
	Enabled        bool
	ClientKeyFile  string
	ClientCertFile string
	CaFile         string
}

// Initializes the necessary TLS configuration options
func tlsOpt(config *TLSConfig, opts []kgo.Opt) []kgo.Opt {
	if config.Enabled {
		if config.CaFile != "" || config.ClientCertFile != "" || config.ClientKeyFile != "" {
			tc, err := tlscfg.New(
				tlscfg.MaybeWithDiskCA(config.CaFile, tlscfg.ForClient),
				tlscfg.MaybeWithDiskKeyPair(config.ClientCertFile, config.ClientKeyFile),
			)
			if err != nil {
				log.Fatalf("Unable to create TLS config: %v", err)
			}
			opts = append(opts, kgo.DialTLSConfig(tc))
		} else {
			opts = append(opts, kgo.DialTLSConfig(new(tls.Config)))
		}
	}
	return opts
}

// Initializes the necessary SASL configuration options
func saslOpt(config *SASLConfig, opts []kgo.Opt) []kgo.Opt {
	if config.Mechanism != "" || config.Username != "" || config.Password != "" {
		if config.Mechanism == "" || config.Username == "" || config.Password == "" {
			log.Fatalln("All of Mechanism, Username, and Password must be specified if any are")
		}
		method := strings.ToLower(config.Mechanism)
		method = strings.ReplaceAll(method, "-", "")
		method = strings.ReplaceAll(method, "_", "")
		switch method {
		case "plain":
			opts = append(opts, kgo.SASL(plain.Auth{
				User: config.Username,
				Pass: config.Password,
			}.AsMechanism()))
		default:
			log.Fatalf("Unrecognized SASL method: %s", config.Mechanism)
		}
	}
	return opts
}

func createTopic(admin *kadm.Client, topic string, ctx context.Context) {
	topicDetails, err := admin.ListTopics(ctx)
	if err != nil {
		log.Errorf("Unable to list topics: %v", err)
		return
	}
	if !topicDetails.Has(topic) {
		resp, _ := admin.CreateTopics(ctx, 1, 1, nil, topic)
		for _, ctr := range resp {
			if ctr.Err != nil {
				log.Warnf("Unable to create topic '%s': %s", ctr.Topic, ctr.Err)
			} else {
				log.Infof("Created topic '%s'", ctr.Topic)
			}
		}
	} else {
		log.Infof("Topic '%s' already exists", topic)
	}
}

func produce(client *kgo.Client, topic string, ctx context.Context) {
	var wg sync.WaitGroup
	for i := 1; i < 25; i++ {
		wg.Add(1)
		record := &kgo.Record{Topic: topic, Value: []byte(fmt.Sprintf("This is event %d", i))}
		client.Produce(ctx, record, func(record *kgo.Record, err error) {
			defer wg.Done()
			if err != nil {
				if err == context.Canceled {
					log.Infof("Received interrupt: %v", err)
					return
				}
				log.Errorf("Record has a produce error: %v", err)
			} else {
				log.Infof("Produced record, Offset: %d, Value: %s", record.Offset, record.Value)
			}
		})
	}
	wg.Wait()
}

func consume(client *kgo.Client, ctx context.Context) {
	for {
		fetches := client.PollFetches(ctx)
		if errors := fetches.Errors(); len(errors) > 0 {
			for _, e := range errors {
				if e.Err == context.Canceled {
					log.Infof("Received interrupt: %v", e.Err)
					return
				}
				log.Errorf("Poll error: %v", e)
			}
		}
		iter := fetches.RecordIter()
		for !iter.Done() {
			record := iter.Next()
			log.Infof("Consumed record, Offset: %d, Value: %s", record.Offset, record.Value)
		}
		err := client.CommitUncommittedOffsets(ctx)
		if err != nil {
			if err == context.Canceled {
				log.Infof("Received interrupt: %v", err)
				return
			}
			log.Errorf("Unable to commit offsets: %v", err)
		}
		client.AllowRebalance()
	}
}

func main() {
	brokers := flag.String("brokers", "localhost:9092", "Comma-separated list of brokers.")
	topic := flag.String("topic", "test", "Topic to produce to and consume from.")
	groupName := flag.String("group", "test-group", "Consumer group name.")
	username := flag.String("username", "", "SASL username.")
	password := flag.String("password", "", "SASL password.")
	flag.Parse()

	tlsConfig := &TLSConfig{
		Enabled: true,
	}

	saslConfig := &SASLConfig{
		Mechanism: "plain",
		Username:  *username,
		Password:  *password,
	}

	opts := []kgo.Opt{}
	opts = append(opts,
		kgo.SeedBrokers(strings.Split(*brokers, ",")...),
		kgo.ConsumeTopics(*topic),
		kgo.ConsumerGroup(*groupName),
		kgo.ConsumeResetOffset(kgo.NewOffset().AtStart()),
		kgo.DisableAutoCommit(),
		kgo.BlockRebalanceOnPoll(),
	)
	opts = tlsOpt(tlsConfig, opts)
	opts = saslOpt(saslConfig, opts)

	client, err := kgo.NewClient(opts...)
	if err != nil {
		log.Fatalf("Unable to load client: %v", err)
	}
	defer client.Close()

	// Check connectivity to cluster
	if err = client.Ping(context.Background()); err != nil {
		log.Fatalf("Unable to ping cluster: %s", err.Error())
	}
	log.Infoln("Connected to cluster")

	admin := kadm.NewClient(client)
	defer admin.Close()
	brokerDetails, err := admin.ListBrokers(context.Background())
	if err != nil {
		log.Errorf("Unable to list brokers: %v", err)
	}
	for _, broker := range brokerDetails {
		brokerJson, _ := json.Marshal(broker)
		log.Infof("> Broker: %s", string(brokerJson))
	}

	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt)
	createTopic(admin, *topic, ctx)
	produce(client, *topic, ctx)
	consume(client, ctx)
	stop()
}
