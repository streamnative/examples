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

/**
 * Common flags for dealing with a topic.
 */
public class TopicFlags extends PulsarClientFlags {

    @Parameter(
        names = {
            "-t", "--topic"
        },
        description = "Pulsar topic name",
        required = true
    )
    public String topic;

    @Parameter(
        names = {
            "-n", "--num-messages"
        },
        description = "Number of messages to produce or consumwe"
    )
    public int numMessages = 10;

}
