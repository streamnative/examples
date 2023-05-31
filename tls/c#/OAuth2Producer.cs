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

            var client = await new PulsarClientBuilder()
                .ServiceUrl(serviceUrl)
                .Authentication(AuthenticationFactoryOAuth2.ClientCredentials(issuerUrl, audience, fileUri))
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