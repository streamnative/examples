package org.apache.pulsar.sn;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.common.schema.KeyValue;
import org.apache.pulsar.sr.kafka.KafkaSchemaInfoProviderFactory;
import org.apache.pulsar.sr.kafka.KafkaSchemas;

import java.util.Map;

public class SchemaRegistryKVDemo {

    public static void main(String[] args) throws Exception {
        // set Kafka schema registry related configurations
        Map<String, String> srConfig = new HashedMap<>();
        srConfig.put("schema.registry.url", "http://localhost:8001");

        PulsarClient client = PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650")
                .schemaInfoProviderFactory(new KafkaSchemaInfoProviderFactory(srConfig))
                .build();

        String topic = "kv-test";

        Schema<KeyValue<String, User>> schema = KafkaSchemas.KV(
                Schema.STRING, KafkaSchemas.JSON(User.class));

        Producer<KeyValue<String, User>> producer = client.newProducer(schema)
                .topic(topic)
                .create();

        Consumer<KeyValue<String, User>> consumer = client.newConsumer(schema)
                .topic(topic)
                .subscriptionName("sub")
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscribe();

        for (int i = 0; i < 10; i++) {
            producer.send(new KeyValue<>("key-" + i, new User("name-" + i, 10 + i)));
        }

        for (int i = 0; i < 10; i++) {
            Message<KeyValue<String, User>> message = consumer.receive();
            consumer.acknowledge(message);
            System.out.println("receive msg " + message.getValue().getKey() + " " + message.getValue().getValue());
        }

        client.close();
    }

}
