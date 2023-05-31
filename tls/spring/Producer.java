@SpringBootApplication
public class PulsarBootHelloWorld {

    public static void main(String[] args) {
        SpringApplication.run(PulsarBootHelloWorld.class, args);
    }

    @Bean
    ApplicationRunner runner(PulsarTemplate<String> pulsarTemplate) {
        return (args) -> pulsarTemplate.send("persistent://public/default/test-topic", "Hello Pulsar World!");
    }

}