@SpringBootApplication
public class PulsarBootHelloWorld {

    public static void main(String[] args) {
        SpringApplication.run(PulsarBootHelloWorld.class, args);
    }

    @PulsarListener(subscriptionName = "test-sub", topics = "persistent://public/default/test-topic")
    void listen(String message) {
        System.out.println("Message Received: " + message);
    }

}