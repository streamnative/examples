The `taxidata` tool supports ingesting green or yellow NYC Taxi data to a Pulsar topic to serve as an example dataset. To obtain a data URL, right-click the data record and choose **Copy Link Address**.

For more details about the dataset, see [TLC Trip Record Data](https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page).

## Setup

- Prepare a working Pulsar cluster. For details about how to create a Pulsar cluster through the StreamNative Cloud Manager, see [Create Pulsar Cluster](https://docs.streamnative.io/cloud/stable/use/cluster#create-cluster-through-streamnative-cloud-manager).

- Build the `taxidata` tool.

  1. Clone this repository from GitHub to your local computer.

		```bash
		git clone https://github.com/streamnative/examples
		cd examples/nyctaxi/taxidata
		```

  2. Build the `taxidata` tool.

		```bash
		go build
		```

## Usage

This section describes `taxidata` configurations and gives an example about how to use the `taxidata` tool to load data to Pulsar topics.

### Configurations

This table lists parameters available for the `taxidata` command.

| Parameter | Description | Default |
| --- | --- | --- |
| `--dataType` | The type of data to ingest. Available values are `green`, `yellow` or `both`. | `both` |
| `--greenDataUrl` | The URL to get the green Taxi data. | https://s3.amazonaws.com/nyc-tlc/trip+data/green_tripdata_2019-01.csv |
| `--maxRecordNumber` | The maximum number of messages to ingest. If it is not specified, all records will be ingested. | 10000 |
| `--pulsarUrl` | The URL to connect to the Pulsar cluster. | N/A|
| `--speed` | The speed for ingestion（messages per second). The `speed` parameter is used to specify the maximum ingestion speed. The actual ingestion rate depends on your network conditions. | 100 |
| `--topicNameGreen` | The topic to which the green Taxi data is produced. | `taxidata-green` |
| `--topicNameYellow` | The topic to which the yellow Taxi data is produced. | `taxidata-yellow` |
| `--verbose` | the log data. | `false` |
| `--yellowDataUrl` | The URL to get the yellow Taxi data. | https://s3.amazonaws.com/nyc-tlc/trip+data/yellow_tripdata_2019-01.csv) |

This table lists authentication parameters supported by the `taxidata` tool.

| Parameter | Description |
| --- | --- |
| `--auth-plugin` | The plugin used for the plugin authentication method. |
| `--auth-params` | The parameters  for the plugin authentication method. |
| `--token` | The token for the token authentication method. |
| `--token-file` | The file with a token for the token authentication method. |
| `--tls-cert-file` | The file with a TLS certificate for the TLS authentication method. |
| `--tls-key-file` | The file with a TLS private key for the TLS authentication method. |
| `--oauth2-issuer` | The issuer endpoint for the OAuth2 authentication method. |
| `--oauth2-audience` | The audience identifier for the OAuth2 authentication method. |
| `--oauth2-key-file` | The file with client credentials for the OAuth2 authentication method. |

### Example

This example shows how to use the `taxidata` tool to ingest both green and yellow NYC Taxi data to topics `public/default/taxidata-green` and `public/default/taxidata-yellow` in StreamNative Cloud.

1. Create a Pulsar cluster through [StreamNative Cloud Manager](https://console.streamnative.cloud/login). For details, see [Create Pulsar Cluster](https://docs.streamnative.io/cloud/stable/use/cluster#create-cluster-through-streamnative-cloud-manager).

2. Create a service account with the super-admin permission for the cluster and download the key file of the service account to your local computer. For details, see [Work with service account](https://docs.streamnative.io/cloud/stable/managed-access/service-account#work-with-service-account-through-streamnative-cloud-manager).

3. Create a Flink cluster. For details, see [Create Flink cluster](https://docs.streamnative.io/cloud/stable/compute/flink-sql.md#create-flink-cluster). The Flink cluster is associated with the Pulsar cluster.

4. Connect to the Pulsar cluster through the OAuth2 authentication plugin.

	```bash
	taxidata \
	--oauth2-issuer https://auth.streamnative.cloud  \
	--oauth2-audience urn:sn:pulsar:pulsar-namespace-name:pulsar-instance-name \
	--oauth2-key-file /path/to/key/file.json \
	--pulsarUrl BROKER_SERVICE_URL
	```

5. Load the NYC Taxi data to the `public/default/greenTaxi` and `public/default/yellowTaxi` topics in StreamNative Cloud.

	```bash
	taxidata \
	--topicNameGreen public/default/greenTaxi \
	--topicNameYellow public/default/yellowTaxi \
	--oauth2-issuer https://auth.streamnative.cloud  \
	--oauth2-audience urn:sn:pulsar:pulsar-namespace-name:pulsar-instance-name \
	--oauth2-key-file /path/to/key/file.json \
	--pulsarUrl BROKER_SERVICE_URL
	```

6. Execute one or more FLink SQL queries through the StreamNative Cloud Manager.

   1. On the left pane of the StreamNative Cloud Manager, click **SQL**.

   2. Select the Flink database (Flink cluster) and Flink catalog (Pulsar cluster).

   3. Select the `public/default` table.

   4. Write one or more SQL statements on the **SQL Editor** window and then click **Run**.

7. Scroll down the page to check the query results at the **Result** area.
