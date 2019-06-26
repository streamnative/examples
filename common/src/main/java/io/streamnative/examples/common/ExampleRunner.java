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

import org.apache.bookkeeper.tools.framework.Cli;
import org.apache.bookkeeper.tools.framework.CliSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An example runner that handles command line parsing.
 */
public abstract class ExampleRunner<T extends PulsarClientFlags> {

    private static final Logger log = LoggerFactory.getLogger(ExampleRunner.class);

    protected abstract String name();

    protected abstract String description();

    protected abstract T flags();

    protected abstract void run(T flags) throws Exception;

    public void run(String[] args) {
        CliSpec.Builder<T> specBuilder = CliSpec.<T>newBuilder()
            .withName(name())
            .withDescription(description())
            .withFlags(flags())
            .withRunFunc(flags -> {
                try {
                    run(flags);
                    return true;
                } catch (Exception e) {
                    log.error("Failed to execute example `{}`", name(), e);
                    System.err.println("Failed to execute example `" + name() + "` :");
                    e.printStackTrace();
                    return false;
                }
            });
        CliSpec<T> spec = specBuilder.build();
        int retCode = Cli.runCli(spec, args);
        Runtime.getRuntime().exit(retCode);
    }


}
