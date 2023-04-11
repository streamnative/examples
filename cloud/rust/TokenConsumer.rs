
use futures::TryStreamExt;
use pulsar::{Authentication, Consumer, ConsumerOptions, Pulsar, SubType, TokioExecutor};
use pulsar::consumer::InitialPosition;

#[tokio::main]
async fn main() -> Result<(), pulsar::Error> {
    env_logger::init();

    let addr = "{{ SERVICE_URL }}".to_string();
    let mut builder = Pulsar::builder(addr, TokioExecutor);
    let token = "{{ AUTH_PARAMS_TOKE }}".to_string();
    builder = builder.with_auth(Authentication {
        name: "token".to_string(),
        data: token.into_bytes(),
    });

    let pulsar: Pulsar<_> = builder.build().await?;
    let mut consumer: Consumer<String, _> = pulsar
        .consumer()
        .with_topic("persistent://public/default/test-topic")
        .with_subscription_type(SubType::Exclusive)
        .with_subscription("test-sub")
        .with_options(ConsumerOptions::default()
            .with_initial_position(InitialPosition::Earliest))
        .build()
        .await?;

    let mut counter = 0usize;
    while let Some(msg) = consumer.try_next().await? {
        consumer.ack(&msg).await?;
        let payload = match msg.deserialize() {
            Ok(payload) => payload,
            Err(e) => {
                println!("could not deserialize message: {:?}", e);
                break;
            }
        };

        counter += 1;
        println!("Received message '{:?}' id='{:?}'", payload, msg.message_id());

        if counter > 10 {
            consumer.close().await.expect("Unable to close consumer");
            break;
        }
    }

    Ok(())
}
