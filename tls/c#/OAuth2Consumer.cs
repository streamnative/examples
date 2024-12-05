using System;
using System.IO;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using Pulsar.Client.Api;
using Pulsar.Client.Common;

namespace CsharpExamples
{
    internal class Oauth2
    {
        internal static async Task RunOauth()
        {
            var fileUri = new Uri("{{ file://YOUR-KEY-FILE-PATH }}");
            var issuerUrl = new Uri("https://auth.streamnative.cloud/");
            var audience = "{{ YOUR-AUDIENCE }}";

            const string serviceUrl = "{{ SERVICE_URL }}";
            var topicName = "persistent://public/default/test-topic";
            const string subscriptionName = "test-sub";

            var client = await new PulsarClientBuilder()
                .ServiceUrl(serviceUrl)
                .Authentication(AuthenticationFactoryOAuth2.ClientCredentials(issuerUrl, audience, fileUri))
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