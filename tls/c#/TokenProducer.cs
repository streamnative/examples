using System;
using System.Text;
using System.Threading.Tasks;
using Pulsar.Client.Api;

namespace CsharpExamples
{
    internal class JWT
    {
        internal static async Task Producer()
        {
            const string serviceUrl = "{{ SERVICE_URL }}";
            var topicName = "persistent://public/default/test-topic";
            var token = "{{ AUTH_PARAMS_TOKEN }}";

            var client = await new PulsarClientBuilder()
                .ServiceUrl(serviceUrl)
                .Authentication(AuthenticationFactory.Token(token))
                .BuildAsync();

            var producer = await client.NewProducer()
                .Topic(topicName)
                .CreateAsync();

            for(int i=0; i< 10; i++){
                var messageId = await producer.SendAsync(Encoding.UTF8.GetBytes($"Sent from C# at '{DateTime.Now}'"));
                Console.WriteLine($"MessageId is: '{messageId}'");
            }
        }
    }
}
