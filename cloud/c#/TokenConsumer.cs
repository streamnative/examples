using System;
using System.Text;
using System.Threading.Tasks;
using Pulsar.Client.Api;

namespace CsharpExamples
{
    internal class JWT
    {
        internal static async Task Consumer()
        {
            const string serviceUrl = "{{ SERVICE_URL }}";
            const string subscriptionName = "test-sub";
            var topicName = "persistent://public/default/test-topic";
            var token = "{{ AUTH_PARAMS_TOKE }}";

            var client = await new PulsarClientBuilder()
                .ServiceUrl(serviceUrl)
                .Authentication(AuthenticationFactory.Token(token))
                .BuildAsync();

            var consumer = await client.NewConsumer()
                .Topic(topicName)
                .SubscriptionName(subscriptionName)
                .SubscribeAsync();

            for(int i=0; i< 10; i++){
                var message = await consumer.ReceiveAsync();
                Console.WriteLine($"Received: {Encoding.UTF8.GetString(message.Data)}");

                await consumer.AcknowledgeAsync(message.MessageId);
            }
        }
    }
}
