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
package io.streamnative.examples.flink;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Optional;
import java.util.Properties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.pulsar.FlinkPulsarSink;
import org.apache.flink.streaming.connectors.pulsar.FlinkPulsarSource;
import org.apache.flink.streaming.connectors.pulsar.TopicKeyExtractor;

/**
 * A Flink streaming job that does word counting.
 */
public class PulsarStreamingWordCount {

    public static void main(String[] args) throws Exception {
        final ParameterTool parameterTool = ParameterTool.fromArgs(args);

        if (parameterTool.getNumberOfParameters() < 2) {
            System.err.println("Missing parameters!");
            return;
        }

        // Set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000));
        env.enableCheckpointing(5000);
        env.getConfig().setGlobalJobParameters(parameterTool);
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        String brokerServiceUrl = parameterTool.getRequired("broker-service-url");
        String adminServiceUrl = parameterTool.getRequired("admin-service-url");
        String inputTopic = parameterTool.getRequired("input-topic");
        String outputTopic = parameterTool.get("output-topic", null);
        int parallelism = parameterTool.getInt("parallelism", 1);
        Properties properties = new Properties();
        properties.setProperty("topic", inputTopic);

        FlinkPulsarSource<String> source = new FlinkPulsarSource<>(
            brokerServiceUrl,
            adminServiceUrl,
            new SimpleStringSchema(),
            properties
        ).setStartFromEarliest();

        DataStream<String> stream = env.addSource(source);
        DataStream<WordCount> wc = stream
            .flatMap((FlatMapFunction<String, WordCount>) (line, collector) -> {
                for (String word : line.split("\\s")) {
                    collector.collect(new WordCount(word, 1));
                }
            })
            .returns(WordCount.class)
            .keyBy("word")
            .timeWindow(Time.seconds(5))
            .reduce((ReduceFunction<WordCount>) (c1, c2) ->
                new WordCount(c1.word, c1.count + c2.count));

        if (null != outputTopic) {
            wc.addSink(new FlinkPulsarSink<>(
                brokerServiceUrl,
                adminServiceUrl,
                Optional.of(outputTopic),
                new Properties(),
                new TopicKeyExtractor<WordCount>() {
                    @Override
                    public byte[] serializeKey(WordCount wordCount) {
                        return wordCount.word.getBytes(UTF_8);
                    }

                    @Override
                    public String getTopic(WordCount wordCount) {
                        return null;
                    }
                },
                WordCount.class
            ));
        } else {
            wc.print().setParallelism(parallelism);
        }

        env.execute("Pulsar Streaming WordCount");

    }

    /**
     * Data type for words with count.
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class WordCount {

        public String word;
        public long count;

    }

}
