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
import io.streamnative.example.model.Customer;
import io.streamnative.example.model.Signup;
import io.streamnative.example.model.SignupTier;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.apache.pulsar.common.schema.SchemaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@SpringBootApplication
public class SignupApplication {

  private static final Logger log = LoggerFactory.getLogger(SignupApplication.class);

  @Autowired private PulsarTemplate<Signup> signupTemplate;

  @Autowired private PulsarTemplate<Customer> customerTemplate;

  @Autowired private SignupGenerator signupGenerator;

  public static void main(String[] args) {
    SpringApplication.run(SignupApplication.class, args);
  }

  @Scheduled(initialDelay = 5000, fixedRate = 5000)
  void publishSignupData() throws PulsarClientException {
    Signup signup = signupGenerator.generate();
    signupTemplate.setSchema(JSONSchema.of(Signup.class));
    signupTemplate.send(signup);
  }

  @PulsarListener(
      subscriptionName = "signup-consumer",
      topics = "signups-topic",
      schemaType = SchemaType.JSON)
  void filterSignups(Signup signup) throws PulsarClientException {
    log.info(
        "{} {} ({}) just signed up for {} tier",
        signup.firstName(),
        signup.lastName(),
        signup.companyEmail(),
        signup.signupTier());

    if (signup.signupTier() == SignupTier.ENTERPRISE) {
      Customer customer = Customer.from(signup);
      customerTemplate.setSchema(JSONSchema.of(Customer.class));
      customerTemplate.send("customer-success", customer);
    }
  }

  @PulsarListener(
      subscriptionName = "customer-consumer",
      topics = "customer-success",
      schemaType = SchemaType.JSON)
  void alertCustomerSuccess(Customer customer) {
    log.info(
        "## Start the onboarding for {} - {} {} ({}) - {} ##",
        customer.companyName(),
        customer.firstName(),
        customer.lastName(),
        customer.phoneNumber(),
        customer.companyEmail());
  }
}
