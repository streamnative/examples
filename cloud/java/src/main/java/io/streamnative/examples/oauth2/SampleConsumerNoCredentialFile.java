package io.streamnative.examples.oauth2;

import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2;

public class SampleConsumerNoCredentialFile {

    public static void main(String[] args) throws Exception {
        JCommanderPulsar jct = new JCommanderPulsar();
        JCommander jCommander = new JCommander(jct, args);
        if (jct.help) {
            jCommander.usage();
            return;
        }

        String topic = "persistent://public/default/topic-1";
        AuthenticationOAuth2 authenticationOAuth2 = new AuthenticationOAuth2();
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> params = Maps.newHashMap();
        Map<String, String> data = Maps.newHashMap();
        data.put("client_id", jct.clientId);
        data.put("client_secret", jct.clientSecret);
        params.put("privateKey", "data:application/json;base64,"
                + new String(Base64.getEncoder().encode(
                        objectMapper.writeValueAsString(data).getBytes(StandardCharsets.UTF_8))));
        params.put("issuerUrl", jct.issuerUrl);
        params.put("audience", jct.audience);
        params.put("scope", jct.scope);
        authenticationOAuth2.configure(objectMapper.writeValueAsString(params));
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(jct.serviceUrl)
                .authentication(authenticationOAuth2)
                .build();

        Consumer<byte[]> consumer = client.newConsumer(Schema.BYTES)
                .topic(topic)
                .subscriptionName("sub-1")
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscribe();

        for (int i = 0; i < 10; i++) {
            Message<byte[]> msg = consumer.receive();
            consumer.acknowledge(msg);
            System.out.println("Receive message " + new String(msg.getData()));
        }

        consumer.close();
        client.close();
    }
}
