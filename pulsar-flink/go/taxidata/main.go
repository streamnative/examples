package main

import (
	"context"
	"encoding/csv"
	"fmt"
	"github.com/apache/pulsar-client-go/pulsar"
	"io"
	"net/http"
	"strconv"
	"sync"
	"time"

	"github.com/spf13/cobra"
)

/** Green Taxi Data Json Schema
{
   "type":"record",
   "name":"NYCTaxiDataSchame",
   "namespace":"io.streamnative.pulsar.avro.taxidata",
   "doc":"This is an avro schema for NYC taxi data.",
   "fields":[
      {
         "name":"VendorID",
         "type":"int"
      },
      {
         "name":"PickUpDateTime",
         "type":"string"
      },
      {
         "name":"DropOffDateTime",
         "type":"string"
      },
      {
         "name":"Flag",
         "type":"string"
      },
      {
         "name":"RatecodeID",
         "type":"int"
      },
      {
         "name":"PickUpLocationID",
         "type":"int"
      },
      {
         "name":"DropOffLocationID",
         "type":"int"
      },
      {
         "name":"PassengerCount",
         "type":"int"
      },
      {
         "name":"TripDistance",
         "type":"double"
      },
      {
         "name":"FareAmount",
         "type":"double"
      },
      {
         "name":"Extra",
         "type":"double"
      },
      {
         "name":"MTATax",
         "type":"double"
      },
      {
         "name":"TipAmount",
         "type":"double"
      },
      {
         "name":"TollsAmount",
         "type":"double"
      },
      {
         "name":"EhailFee",
         "type": "double"
      },
      {
         "name":"ImprovementSurcharge",
         "type":"double"
      },
      {
         "name":"TotalAmount",
         "type":"double"
      },
      {
         "name":"PaymentType",
         "type":"int"
      },
      {
         "name":"TripType",
         "type":"int"
      },
      {
         "name":"CongestionSurcharge",
         "type": "double"
      }
   ]
}
*/

type GreenTaxiData struct {
	VendorID             int
	PickUpDateTime       string
	DropOffDateTime      string
	Flag                 string
	RatecodeID           int
	PickUpLocationID     int
	DropOffLocationID    int
	PassengerCount       int
	TripDistance         float64
	FareAmount           float64
	Extra                float64
	MTATax               float64
	TipAmount            float64
	TollsAmount          float64
	EhailFee             float64
	ImprovementSurcharge float64
	TotalAmount          float64
	PaymentType          int
	TripType             int
	CongestionSurcharge  float64
}

/* Yello Taxi Data Json Schema
{
   "type":"record",
   "name":"NYCTaxiDataSchame",
   "namespace":"io.streamnative.pulsar.avro.taxidata",
   "doc":"This is an avro schema for NYC taxi data.",
   "fields":[
      {
         "name":"VendorID",
         "type":"int"
      },
      {
         "name":"PickUpDateTime",
         "type":"string"
      },
      {
         "name":"DropOffDateTime",
         "type":"string"
      },
      {
         "name":"PassengerCount",
         "type":"int"
      },
      {
         "name":"TripDistance",
         "type":"double"
      },
      {
         "name":"Flag",
         "type":"string"
      },
	  {
         "name":"RatecodeID",
         "type":"int"
      },
      {
         "name":"PickUpLocationID",
         "type":"int"
      },
      {
         "name":"DropOffLocationID",
         "type":"int"
      },
      {
         "name":"PaymentType",
         "type":"int"
      },
      {
         "name":"FareAmount",
         "type":"double"
      },
      {
         "name":"Extra",
         "type":"double"
      },
      {
         "name":"MTATax",
         "type":"double"
      },
      {
         "name":"TipAmount",
         "type":"double"
      },
      {
         "name":"TollsAmount",
         "type":"double"
      },
      {
         "name":"ImprovementSurcharge",
         "type":"double"
      },
      {
         "name":"TotalAmount",
         "type":"double"
      },
      {
         "name":"CongestionSurcharge",
         "type":"double"
      }
   ]
}
*/

type YellowTaxiData struct {
	VendorID             int
	PickUpDateTime       string
	DropOffDateTime      string
	PassengerCount       int
	TripDistance         float64
	RatecodeID           int
	Flag                 string
	PickUpLocationID     int
	DropOffLocationID    int
	PaymentType          int
	FareAmount           float64
	Extra                float64
	MTATax               float64
	TipAmount            float64
	TollsAmount          float64
	ImprovementSurcharge float64
	TotalAmount          float64
	CongestionSurcharge  float64
}

type DataType string

type ingestionOps struct {
	pulsarUrl  string
	pulsarAuthParameter  string
	topicNameGreen string
	topicNameYellow string
	speed int
	dataType     string
	greenDataUrl string
	yellowDataUrl string
	maxRecordNumber int64
	verbose bool
}

type ingestionArgs struct {
	verbose bool
	pulsarUrl  string
	pulsarAuthParameter  string
	url string
	schema string
	topic string
	speed int
	maxRecordNumber int64
	dataType DataType
}

const (
	YellowTaxi DataType = "Yellow"
	GreenTaxi  DataType = "Green"
)

const (
	GreenTaxiSchema  = "{\"type\":\"record\",\"name\":\"NYCTaxiDataSchema\",\"namespace\":\"io.streamnative.pulsar.avro.taxidata\",\"doc\":\"This is an avro schema for NYC taxi data.\",\"fields\":[{\"name\":\"VendorID\",\"type\":\"int\"},{\"name\":\"PickUpDateTime\",\"type\":\"string\"},{\"name\":\"DropOffDateTime\",\"type\":\"string\"},{\"name\":\"Flag\",\"type\":\"string\"},{\"name\":\"RatecodeID\",\"type\":\"int\"},{\"name\":\"PickUpLocationID\",\"type\":\"int\"},{\"name\":\"DropOffLocationID\",\"type\":\"int\"},{\"name\":\"PassengerCount\",\"type\":\"int\"},{\"name\":\"TripDistance\",\"type\":\"double\"},{\"name\":\"FareAmount\",\"type\":\"double\"},{\"name\":\"Extra\",\"type\":\"double\"},{\"name\":\"MTATax\",\"type\":\"double\"},{\"name\":\"TipAmount\",\"type\":\"double\"},{\"name\":\"TollsAmount\",\"type\":\"double\"},{\"name\":\"EhailFee\",\"type\":\"double\" },{\"name\":\"ImprovementSurcharge\",\"type\":\"double\"},{\"name\":\"TotalAmount\",\"type\":\"double\"},{\"name\":\"PaymentType\",\"type\":\"int\"},{\"name\":\"TripType\",\"type\":\"int\"},{\"name\":\"CongestionSurcharge\",\"type\":\"double\" }]}"
	YellowTaxiSchema = "{\"type\":\"record\",\"name\":\"NYCTaxiDataSchema\",\"namespace\":\"io.streamnative.pulsar.avro.taxidata\",\"doc\":\"This is an avro schema for NYC taxi data.\",\"fields\":[{\"name\":\"VendorID\",\"type\":\"int\"},{\"name\":\"PickUpDateTime\",\"type\":\"string\"},{\"name\":\"DropOffDateTime\",\"type\":\"string\"},{\"name\":\"PassengerCount\",\"type\":\"int\"},{\"name\":\"TripDistance\",\"type\":\"double\"},{\"name\":\"RatecodeID\",\"type\":\"int\"},{\"name\":\"Flag\",\"type\":\"string\"},{\"name\":\"PickUpLocationID\",\"type\":\"int\"},{\"name\":\"DropOffLocationID\",\"type\":\"int\"},{\"name\":\"PaymentType\",\"type\":\"int\"},{\"name\":\"FareAmount\",\"type\":\"double\"},{\"name\":\"Extra\",\"type\":\"double\"},{\"name\":\"MTATax\",\"type\":\"double\"},{\"name\":\"TipAmount\",\"type\":\"double\"},{\"name\":\"TollsAmount\",\"type\":\"double\"},{\"name\":\"ImprovementSurcharge\",\"type\":\"double\"},{\"name\":\"TotalAmount\",\"type\":\"double\"},{\"name\":\"CongestionSurcharge\",\"type\":\"double\"}]}"
)

var longHelpText = `
Usage: taxiData [ARGS]
Ingest NYC taxi data to a pulsar topic.

The data can be found at: https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page
The predefine data schema works for green/yellow taxi data start from January 2019.
`

func main() {
	opts := ingestionOps{}
	var cmd = &cobra.Command{
		Use:     "taxiData",
		Short:   "Ingest NYC taxi data to a pulsar topic",
		Long:    longHelpText,
		RunE:    opts.Run,
	}
	opts.Add(cmd)

	_ = cmd.Execute()
}

func (o *ingestionOps) Run(_ *cobra.Command, args []string) error {
	var ingestArgs [] ingestionArgs
	var wg sync.WaitGroup

	switch o.dataType {
	case "green":
		if o.topicNameGreen == "" || o.greenDataUrl == "" {
			return fmt.Errorf("need to specify both topicNameGreen and greenDataUrl to ingest green taxi data")
		}
		ingestArgs = append(ingestArgs, ingestionArgs{o.verbose,o.pulsarUrl, o.pulsarAuthParameter,
			o.greenDataUrl, GreenTaxiSchema, o.topicNameGreen, o.speed, o.maxRecordNumber, GreenTaxi})
		break
	case "yellow":
		if o.topicNameYellow == "" || o.yellowDataUrl == "" {
			return fmt.Errorf("need to specify both topicNameYellow and yellowDataUrl to ingest yellow taxi data")
		}
			ingestArgs = append(ingestArgs, ingestionArgs{o.verbose,o.pulsarUrl, o.pulsarAuthParameter,
			o.yellowDataUrl, YellowTaxiSchema, o.topicNameYellow, o.speed, o.maxRecordNumber,YellowTaxi})
		break
	case "both":
		if o.topicNameYellow == "" || o.yellowDataUrl == "" || o.topicNameGreen == "" || o.greenDataUrl == "" {
			return fmt.Errorf("need to specify topicNameYellow,yellowDataUrl,topicNameGreen and greenDataUrl to " +
				"ingest both green and yellow taxi data")
		}
		ingestArgs = append(ingestArgs, ingestionArgs{o.verbose,o.pulsarUrl, o.pulsarAuthParameter,
			o.greenDataUrl, GreenTaxiSchema, o.topicNameGreen, o.speed, o.maxRecordNumber,GreenTaxi})
		ingestArgs = append(ingestArgs, ingestionArgs{o.verbose,o.pulsarUrl, o.pulsarAuthParameter,
			o.yellowDataUrl, YellowTaxiSchema, o.topicNameYellow, o.speed, o.maxRecordNumber,YellowTaxi})
		break
	default:
		return fmt.Errorf("%s not a valid dataType option, allowed values are 'green','yellow' or 'both'", o.dataType)
	}

	wg.Add(len(ingestArgs))
	for _, ingestArg := range ingestArgs {
		go ingestData(ingestArg.verbose,
			ingestArg.pulsarUrl,
			ingestArg.pulsarAuthParameter,
			ingestArg.url,
			ingestArg.schema,
			ingestArg.topic,
			ingestArg.speed,
			ingestArg.dataType,
			ingestArg.maxRecordNumber,
			&wg,
		)
	}
	wg.Wait()
	return nil
}

func (o *ingestionOps) Add(cmd *cobra.Command) {
	cmd.Flags().StringVar(&o.pulsarUrl, "pulsarUrl", "", "Url of pulsar cluster to connect")
	_ = cmd.MarkFlagRequired("pulsarUrl")
	cmd.Flags().StringVar(&o.pulsarAuthParameter, "pulsarAuthParameter", "", "Auth data to use if using ssl")
	cmd.Flags().StringVar(&o.topicNameGreen, "topicNameGreen", "taxidata-green", "Topic to ingest green taxi data to")
	cmd.Flags().StringVar(&o.topicNameYellow, "topicNameYellow", "taxidata-yellow", "Topic to ingest yellow taxi data to")
	cmd.Flags().IntVar(&o.speed,"speed", 100, "Max speed for ingestion as number of message/second, e.g. it can't go beyond this speed but could go below it.")
	cmd.Flags().Int64Var(&o.maxRecordNumber,"maxRecordNumber", 10000, "Maximum number of message to ingest, " +
		"if not specified will ingest whole record set.")
	cmd.Flags().StringVar(&o.dataType, "dataType", "both", "Type of data to ingest, can be 'green', 'yellow' or 'both'")
	cmd.Flags().StringVar(&o.greenDataUrl, "greenDataUrl", "https://s3.amazonaws.com/nyc-tlc/trip+data/green_tripdata_2019-01.csv",
		"Url to get green taxi data")
	cmd.Flags().StringVar(&o.yellowDataUrl, "yellowDataUrl", "https://s3.amazonaws.com/nyc-tlc/trip+data/yellow_tripdata_2019-01.csv",
		"Url to get yellow taxi data")
	cmd.Flags().BoolVar(&o.verbose, "verbose", false, "Log data")
}

func ingestData(verbose bool, pulsar, auth, url, schema, topic string, speed int, dataType DataType, maxRecordNumber int64, wg *sync.WaitGroup) {
	defer wg.Done()
	err := ingestToPulsar(verbose, pulsar, auth, url, schema, topic, speed, maxRecordNumber, dataType)
	if err != nil {
		panic(err)
	}
	fmt.Println("Ingested: " + url)
}

func ingestToPulsar(verbose bool, pulsarUrl, auth, url, schema, topic string, speed int, maxRecordNumber int64, dataType DataType) error {

	// Get the data
	resp, err := http.Get(url)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	reader := csv.NewReader(resp.Body)

	// Read the records
	header, err := reader.Read()
	if err != nil {
		fmt.Println("An error encountered ::", err)
		return err
	}
	if verbose {
		fmt.Printf("Headers : %v \n", header)
	}

	client, err := pulsar.NewClient(getClientOptions(pulsarUrl, auth))
	if err != nil {
		fmt.Println("An error encountered ::", err)
		return err
	}
	jsonSchema := pulsar.NewJSONSchema(schema, nil)
	producer, err := client.CreateProducer(pulsar.ProducerOptions{
		Topic:  topic,
		Schema: jsonSchema,
	})
	if err != nil {
		fmt.Println("An error encountered ::", err)
		return err
	}

	i := int64(0)

	limit := make(chan int)
	stop := make(chan bool)
	// Making permit/second at fix speed.
	go func() {
		for range time.Tick(1 * time.Second) {
			select {
			case <-stop:
				return
			default:
				limit <- speed
			}
		}
	}()

	record, err := reader.Read()
	for permit := range limit {
		c := 0
		if verbose {
			fmt.Printf("%d message read from source at %s", permit, time.Now())
		}
		for c < permit {
			c++
			i++
			if err == io.EOF || i > maxRecordNumber {
				close(limit)
				stop <- true
				break // reached end of the file
			} else if err != nil {
				fmt.Println("An error encountered ::", err)
				return err
			}
			produceWithRetry(verbose, producer, record, 3, dataType)
			if verbose {
				fmt.Printf("Row %d : %v \n", i, record)
			}
			if i % 100 == 0 {
				err = producer.Flush()
				if err != nil {
					fmt.Println(err)
				}
			}
			record, err = reader.Read()
		}
		producer.Flush()
	}
	return err
}

func produceWithRetry(verbose bool, producer pulsar.Producer, taxiRecord []string, retry int, dataType DataType) {
	if retry >= 0 {
		producer.SendAsync(context.Background(), &pulsar.ProducerMessage{
			Value: buildMsg(taxiRecord, dataType),
		}, func(id pulsar.MessageID, message *pulsar.ProducerMessage, e error) {
			if e != nil {
				produceWithRetry(verbose, producer, taxiRecord, retry-1, dataType)
				fmt.Printf("Got error publishing message %v with exception %s, retry left %d", message, e, retry)
			} else {
				if verbose {
					fmt.Printf("Published message %s", id)
				}
			}
		})
	}
}

func getClientOptions(pulsarUrl, auth string) pulsar.ClientOptions {
	pco := pulsar.ClientOptions{}
	pco.URL = pulsarUrl
	if auth == "" {
		pco.Authentication = nil
	} else {
		pco.Authentication = &auth
	}
	return pco
}

func buildMsg(record []string, dataType DataType) interface{} {
	switch dataType {
	case GreenTaxi:
		return buildGreenTaxiDataMsg(record)
		break
	case YellowTaxi:
		return buildYellowTaxiDataMsg(record)
		break
	}
	return nil
}

func buildGreenTaxiDataMsg(record []string) GreenTaxiData {
	return GreenTaxiData{
		VendorID:             getInt(record[0]),
		PickUpDateTime:       record[1],
		DropOffDateTime:      record[2],
		Flag:                 record[3],
		RatecodeID:           getInt(record[4]),
		PickUpLocationID:     getInt(record[5]),
		DropOffLocationID:    getInt(record[6]),
		PassengerCount:       getInt(record[7]),
		TripDistance:         getDouble(record[8]),
		FareAmount:           getDouble(record[9]),
		Extra:                getDouble(record[10]),
		MTATax:               getDouble(record[11]),
		TipAmount:            getDouble(record[12]),
		TollsAmount:          getDouble(record[13]),
		EhailFee:             getDouble(record[14]),
		ImprovementSurcharge: getDouble(record[15]),
		TotalAmount:          getDouble(record[16]),
		PaymentType:          getInt(record[17]),
		TripType:             getInt(record[18]),
		CongestionSurcharge:  getDouble(record[19]),
	}
}

func buildYellowTaxiDataMsg(record []string) YellowTaxiData {
	return YellowTaxiData{
		VendorID:             getInt(record[0]),
		PickUpDateTime:       record[1],
		DropOffDateTime:      record[2],
		PassengerCount:       getInt(record[3]),
		TripDistance:         getDouble(record[4]),
		RatecodeID:           getInt(record[5]),
		Flag:           	  record[6],
		PickUpLocationID:     getInt(record[7]),
		DropOffLocationID:    getInt(record[8]),
		PaymentType:          getInt(record[9]),
		FareAmount:           getDouble(record[10]),
		Extra:                getDouble(record[11]),
		MTATax:               getDouble(record[12]),
		TipAmount:            getDouble(record[13]),
		TollsAmount:          getDouble(record[14]),
		ImprovementSurcharge: getDouble(record[15]),
		TotalAmount:          getDouble(record[16]),
		CongestionSurcharge:  getDouble(record[17]),
	}
}

func getInt(s string) int {
	i, err := strconv.Atoi(s)
	if err != nil {
		return 0
	}
	return i
}

func getDouble(s string) float64 {
	f, err := strconv.ParseFloat(s, 64)
	if err != nil {
		return 0.0
	}
	return f
}

//func consumeData() {
//	client, err := pulsar.NewClient(pulsar.ClientOptions{
//		URL:            "pulsar://localhost:33000",
//		Authentication: nil,
//	})
//	if err != nil {
//		fmt.Println(err)
//	}
//	greenTaxiSchema := "{\"type\":\"record\",\"name\":\"NYCTaxiDataSchame\",\"namespace\":\"io.streamnative.pulsar.avro.taxidata\",\"doc\":\"This is an avro schema for NYC taxi data.\",\"fields\":[{\"name\":\"VendorID\",\"type\":\"int\"},{\"name\":\"PickUpDateTime\",\"type\":\"string\"},{\"name\":\"DropOffDateTime\",\"type\":\"string\"},{\"name\":\"Flag\",\"type\":\"string\"},{\"name\":\"RatecodeID\",\"type\":\"int\"},{\"name\":\"PickUpLocationID\",\"type\":\"int\"},{\"name\":\"DropOffLocationID\",\"type\":\"int\"},{\"name\":\"PassengerCount\",\"type\":\"int\"},{\"name\":\"TripDistance\",\"type\":\"double\"},{\"name\":\"FareAmount\",\"type\":\"double\"},{\"name\":\"Extra\",\"type\":\"double\"},{\"name\":\"MTATax\",\"type\":\"double\"},{\"name\":\"TipAmount\",\"type\":\"double\"},{\"name\":\"TollsAmount\",\"type\":\"double\"},{\"name\":\"EhailFee\",\"type\":\"double\" },{\"name\":\"ImprovementSurcharge\",\"type\":\"double\"},{\"name\":\"TotalAmount\",\"type\":\"double\"},{\"name\":\"PaymentType\",\"type\":\"int\"},{\"name\":\"TripType\",\"type\":\"int\"},{\"name\":\"CongestionSurcharge\",\"type\":\"double\" }]}"
//	jsonSchema := pulsar.NewJSONSchema(greenTaxiSchema, nil)
//	consumer, err := client.Subscribe(pulsar.ConsumerOptions{
//		Topic:                       "nyc-taxi-data222",
//		SubscriptionName:            "my-sub",
//		Schema:                      jsonSchema,
//		SubscriptionInitialPosition: pulsar.SubscriptionPositionEarliest,
//	})
//	msg, err := consumer.Receive(context.Background())
//	taxidata := &GreenTaxiData{}
//	jsonSchema.Decode(msg.Payload(), taxidata)
//	fmt.Println(taxidata)
//}