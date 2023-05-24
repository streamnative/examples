
use pulsar::{Pulsar, TokioExecutor};
use pulsar::authentication::oauth2::{OAuth2Authentication, OAuth2Params};

#[tokio::main]
async fn main() -> Result<(), pulsar::Error> {
    env_logger::init();

    let addr = "{{ SERVICE_URL }}".to_string();
    let mut builder = Pulsar::builder(addr, TokioExecutor);
    builder = builder.with_auth_provider(OAuth2Authentication::client_credentials(OAuth2Params {
        issuer_url: "https://auth.streamnative.cloud/".to_string(),
        credentials_url: "{{ file://YOUR-KEY-FILE-PATH }}".to_string(), // Absolute path of your downloaded key file
        audience: Some("{{ YOUR-AUDIENCE }}".to_string()),
        scope: None,
    }));

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
