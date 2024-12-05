/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamnative.example;

import io.streamnative.example.datagen.SignupGenerator;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class SignupConfiguration {

  @Bean
  SignupGenerator signupGenerator() {
    return new SignupGenerator();
  }

  @Bean
  ProducerInterceptor loggingInterceptor() {
    return new LoggingInterceptor();
  }

  static class LoggingInterceptor implements ProducerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public void close() {
      // no-op
    }

    @Override
    public boolean eligible(Message message) {
      return true;
    }

    @Override
    public Message beforeSend(Producer producer, Message message) {
      return message;
    }

    @Override
    public void onSendAcknowledgement(
        Producer producer, Message message, MessageId msgId, Throwable exception) {
      log.debug("MessageId: {}, Value: {}", message.getMessageId(), message.getValue());
    }
  }
}
