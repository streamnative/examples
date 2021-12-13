package io.streamnative.examples.oauth2;

import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2;

public class SampleProducerNoCredentialFile {

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

        ProducerBuilder<byte[]> producerBuilder = client.newProducer().topic(topic)
                .producerName("my-producer-name");
        Producer<byte[]> producer = producerBuilder.create();

        for (int i = 0; i < 10; i++) {
            String message = "my-message-" + i;
            MessageId msgID = producer.send(message.getBytes());
            System.out.println("Publish " + "my-message-" + i + " and message ID " + msgID);
        }

        producer.close();
        client.close();
    }
}
