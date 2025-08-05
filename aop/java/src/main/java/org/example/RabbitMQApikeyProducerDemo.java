/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.LongString;
import com.rabbitmq.client.SaslMechanism;
import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.DefaultCredentialsRefreshService;
import com.rabbitmq.client.impl.LongStringHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ messaging test.
 */
@Slf4j
public class RabbitMQApikeyProducerDemo {

    private String apikeyHost = "host"; // Replace with your actual RabbitMQ host
    private String port = "5671";
    private String virtualHost = "aoptest"; // please create the namespace public/aoptest, the namespace should only has one bundle
    private String exchange = "ex-fanout";
    private String queue = "qu";
    private int msgCount = 100;
    private DefaultCredentialsRefreshService refreshService;

    private Connection getConnection() throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(apikeyHost);
        connectionFactory.setPort(Integer.parseInt(port));
        connectionFactory.useSslProtocol();
        connectionFactory.setVirtualHost(virtualHost);
        final String apikeyToken = "apikeyToken"; // Replace with your actual API key token

        connectionFactory.setCredentialsProvider(new CredentialsProvider() {
            @Override
            public String getUsername() {
                return null;
            }

            @Override
            public String getPassword() {
                return apikeyToken;
            }
        });
        connectionFactory.setSaslConfig(strings -> new SaslMechanism() {
            @Override
            public String getName() {
                return "token";
            }

            @Override
            public LongString handleChallenge(LongString longString, String s, String s1) {
                return LongStringHelper.asLongString(s1);
            }
        });
        refreshService = new DefaultCredentialsRefreshService.DefaultCredentialsRefreshServiceBuilder().build();
        connectionFactory.setCredentialsRefreshService(refreshService);

        return connectionFactory.newConnection();
    }

    public void start() throws Exception {
        Connection connection = getConnection();
        log.info("get connection");
        var channel = connection.createChannel();
        String ex = exchange;
        String qu = queue;

        channel.exchangeDeclare(ex, BuiltinExchangeType.FANOUT, true);
        channel.queueDeclare(qu, true, false, false, null);
        channel.queueBind(qu, ex, "");
        log.info("declared exchange and queue");

        for (int i = 0; i < msgCount; i++) {
            channel.basicPublish(ex, "", null, ("msg " + i).getBytes());
        }
        log.info("published {} messages", msgCount);

        channel.close();
        connection.close();
        refreshService.close();
    }

    public static void main(String[] args) throws Exception {
        log.info("start producer demo");
        RabbitMQApikeyProducerDemo demo = new RabbitMQApikeyProducerDemo();
        demo.start();
        log.info("finish producer demo");
    }

}
