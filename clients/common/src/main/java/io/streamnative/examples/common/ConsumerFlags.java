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
package io.streamnative.examples.common;

import com.beust.jcommander.Parameter;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.api.proto.PulsarApi.CommandAck.AckType;

/**
 * Common flags for a consumer example.
 */
public class ConsumerFlags extends TopicFlags {

    @Parameter(
        names = {
            "-sn", "--subscription-name"
        },
        description = "Pulsar subscription name",
        required = true
    )
    public String subscriptionName = "test-sub";

    @Parameter(
        names = {
            "-st", "--subscription-type"
        },
        description = "Pulsar subscription type",
        required = true
    )
    public SubscriptionType subscriptionType = SubscriptionType.Exclusive;

    @Parameter(
        names = {
            "-sip", "--subscription-initial-position"
        },
        description = "The initial position for the subscription"
    )
    public SubscriptionInitialPosition subscriptionInitialPosition = SubscriptionInitialPosition.Earliest;

    @Parameter(
        names = {
            "-an", "--ack-every-n-messages"
        },
        description = "Ack every N messages"
    )
    public int ackEveryNMessages = 1;

    @Parameter(
        names = {
            "-at", "--ack-type"
        },
        description = "Ack type"
    )
    public AckType ackType = null;

}
