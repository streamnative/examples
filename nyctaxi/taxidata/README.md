A little tool to ingest [NYC Taxi Data](https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page) to 
a pulsar topic to serve as an example dataset.

## Setup
Prepare a working pulsar cluster following the guide from official site: https://pulsar.apache.org/

Then, clone this repository and build the `taxidata` tool:
```bash
go build
```

## Usage
Use the `taxidata` tool to ingest the NYC Taxi data.
```text
  taxidata [flags]

Flags:
      --dataType string              Type of data to ingest, can be 'green', 'yellow' or 'both' (default "both")
      --greenDataUrl string          Url to get green taxi data (default "https://s3.amazonaws.com/nyc-tlc/trip+data/green_tripdata_2019-01.csv")
  -h, --help                         help for taxiData
      --maxRecordNumber int          Maximum number of message to ingest, if not specified will ingest whole record set. (default 10000)
      --pulsarUrl string             Url of pulsar cluster to connect
      --speed int                    Speed for ingestion as number of message/second (default 100)
      --topicNameGreen string        Topic to ingest green taxi data to (default "taxidata-green")
      --topicNameYellow string       Topic to ingest yellow taxi data to (default "taxidata-yellow")
      --verbose                      Log data (default false)
      --yellowDataUrl string         Url to get yellow taxi data (default "https://s3.amazonaws.com/nyc-tlc/trip+data/yellow_tripdata_2019-01.csv")
```
More detail about the dataset can be found on the [TLC Trip Record Data](https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page) page. 
This tool supports ingesting the green/yellow taxi trip records, starting from January 2019. To obtain a data URL, right-click and choose 'Copy Link Address'.

Notice the speed can only control maximum ingestion speed, actual ingestion rate also depends on network conditions.

This tool supports a variety of authentication providers:
```text
      --auth-plugin string       The plugin to use for plugin authentication
      --auth-params string       The parameters for plugin authentication
      --token string             The token to use for token authentication
      --token-file string        The file with a token to use for token authentication
      --tls-cert-file string     The file with a TLS certificate for TLS authentication
      --tls-key-file string      The file with a TLS private key for TLS authentication
      --oauth2-issuer string     The issuer endpoint for OAuth2 authentication
      --oauth2-audience string   The audience identifier for OAuth2 authentication
      --oauth2-key-file string   The file with client credentials for OAuth2 authentication
```

### Examples 
Ingest both green and yellow taxi data from 2019/01 to topic `public/default/taxidata-green` and to `public/default/taxidata-yellow` at the 
speed of 100 records/sec to a maximum of 1000 records from each data set:
```bash
taxidata --pulsarUrl pulsar://localhost:6650 --maxRecordNumber 1000 --speed 100
```

Ingest green taxi data to the specified topic `my-tenant/my-namespace/taxidata-green`:
```bash
taxidata --pulsarUrl pulsar://localhost:6650 --dataType green --topicNameGreen my-tenant/my-namespace/taxidata-green
```

Ingest yellow taxi data from the specified data URL:
```bash
taxidata --pulsarUrl pulsar://localhost:6650 --dataType yellow --yellowDataUrl https://s3.amazonaws.com/nyc-tlc/trip+data/yellow_tripdata_2019-03.csv
```

Ingest taxi data using a TLS connection with TLS authentication:
```bash
taxidata --pulsarUrl pulsar+ssl://localhost:6651 --tls-cert-file my-role.cert.pem --tls-key-file my-role.key-pk8.pem
```
