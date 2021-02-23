A little tool that can ingest NYC Taxi Data to your pulsar topic as sample data.<br/>
__Prerequisite__: Prepare a working pulsar cluster following the guide from official site: https://pulsar.apache.org/<br/>
For experiment purpose a standalone deployment is recommended: https://pulsar.apache.org/docs/en/standalone/<br/>

__Usage:__<br/>
Clone and build this go package.
Then we can use the taxidata	command to ingest nyc taxi data to pulsar topic as sample data.
```
  taxiData [flags]

Flags:
      --dataType string              Type of data to ingest, can be 'green', 'yellow' or 'both' (default "both")
      --greenDataUrl string          Url to get green taxi data (default "https://s3.amazonaws.com/nyc-tlc/trip+data/green_tripdata_2019-01.csv")
  -h, --help                         help for taxiData
      --maxRecordNumber int          Maximum number of message to ingest, if not specified will ingest whole record set. (default 10000)
      --pulsarAuthParameter string   Auth data to use if using ssl
      --pulsarUrl string             Url of pulsar cluster to connect
      --speed int                    Speed for ingestion as number of message/second (default 100)
      --topicNameGreen string        Topic to ingest green taxi data to (default "taxidata-green")
      --topicNameYellow string       Topic to ingest yellow taxi data to (default "taxidata-yellow")
      --verbose                      Log data (default false)
      --yellowDataUrl string         Url to get yellow taxi data (default "https://s3.amazonaws.com/nyc-tlc/trip+data/yellow_tripdata_2019-01.csv")
```
More detail about nyc taxi data can be found here: https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page. <br /> 
Different data set has different schema and currently this tool only supports ingesting green/yellow taxi data start from January 2019(right click on data set and choose 'Copy Link Address' to get url for the data set).<br /> 

Notice the speed can only control maximum ingestion speed, actual ingestion rate also depends on network.<br /> 

__Sample usage:__<br /> 
```taxidata --pulsarUrl pulsar://localhost:6650 --maxRecordNumber 1000```<br /> 
To ingest both green and yellow taxi data from 2019/01 to default topic public/default/taxidata-green and public/default/taxidata-yellow at the speed of 100 msg/sec and ingest 1000 msg from each data set.

```taxidata --pulsarUrl pulsar://localhost:6650 --maxRecordNumber 1000 --dataType green --topicNameGreen my-tenant/my-namespace/my-taxi-green```<br /> 
To ingest only green taxi data from 2019/01 to topic my-tenant/my-namespace/my-taxi-green at the speed of 100 msg/sec and ingest 1000 msg.

```taxidata --pulsarUrl pulsar+ssl://localhost:6651 --verbose --speed 60 --maxRecordNumber 100000 --dataType yellow --topicNameYellow my-tenant/my-namespace/my-taxi-yellow --yellowDataUrl https://s3.amazonaws.com/nyc-tlc/trip+data/yellow_tripdata_2019-03.csv --pulsarAuthParameter {"type":"sn_service_account","client_id":"xxx","client_secret":"xxx","client_email":"xxx@streamnative.io","issuer_url":"https://streamnative.io"}```<br /> 
To ingest only yellow taxi data from 2019/03 to topic my-tenant/my-namespace/my-taxi-green with authentication enable at pulsar side at the speed of 60 msg/sec and ingest 100000 msg in verbose mode.

