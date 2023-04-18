
use pulsar::{Authentication, Pulsar, TokioExecutor};

#[tokio::main]
async fn main() -> Result<(), pulsar::Error> {
    env_logger::init();

    let addr = "{{ SERVICE_URL }}".to_string();
    let mut builder = Pulsar::builder(addr, TokioExecutor);
    let token = "{{ AUTH_PARAMS_TOKEN }}".to_string();
    builder = builder.with_auth(Authentication {
        name: "token".to_string(),
        data: token.into_bytes(),
    });

    let pulsar: Pulsar<_> = builder.build().await?;
    let mut producer = pulsar
        .producer()
        .with_topic("persistent://public/default/test-topic")
        .build()
        .await?;

    let mut counter = 0usize;
    loop {
        producer
            .send(format!("Hello-{}", counter))
            .await?
            .await
            .unwrap();

        counter += 1;
        println!("{counter} messages");

        if counter > 10 {
            producer.close().await.expect("Unable to close connection");
            break;
        }
    }

    Ok(())
}
