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
import org.apache.bookkeeper.tools.framework.CliFlags;

/**
 * Default example flags
 */
public class PulsarClientFlags extends CliFlags {

    @Parameter(
        names = {
            "-bu", "--binary-service-url"
        },
        description = "Pulsar binary service url"
    )
    public String binaryServiceUrl = "pulsar://localhost:6650";


    @Parameter(
        names = {
            "-hu", "--http-service-url"
        },
        description = "Pulsar http service url"
    )
    public String httpServiceUrl = "http://localhost:8080";

}
