// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package main

import (
	"context"
	"encoding/csv"
	"fmt"
	"github.com/apache/pulsar-client-go/pulsar"
	"github.com/streamnative/examples/nyctaxi/taxidata/pkg/options"
	"github.com/streamnative/examples/nyctaxi/taxidata/pkg/types"
	"io"
	"net/http"
	"sync"
	"time"

	"github.com/spf13/cobra"
)

type ParseFunc func(record []string) interface{}

type ingestionOps struct {
	options.AuthenticationFlags

	pulsarUrl       string
	topicNameGreen  string
	topicNameYellow string
	speed           int
	dataType        string
	greenDataUrl    string
	yellowDataUrl   string
	maxRecordNumber int64
	verbose         bool
}

type ingestionArgs struct {
	url             string
	schema          string
	topic           string
	speed           int
	maxRecordNumber int64
	parser          ParseFunc
}

var longHelpText = `
Usage: taxiData [ARGS]
Ingest NYC taxi data to a pulsar topic.

The data can be found at: https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page
The predefine data schema works for green/yellow taxi data start from January 2019.
`

func main() {
	opts := ingestionOps{}
	var cmd = &cobra.Command{
		Use:   "taxiData",
		Short: "Ingest NYC taxi data to a pulsar topic",
		Long:  longHelpText,
		PreRunE: func(cmd *cobra.Command, args []string) error {
			if err := opts.Validate(); err != nil {
				return err
			}
			cmd.SilenceUsage = true
			return nil
		},
		RunE: opts.Run,
	}
	opts.Add(cmd)

	_ = cmd.Execute()
}

func (o *ingestionOps) Run(_ *cobra.Command, _ []string) error {
	var ingestArgs []ingestionArgs
	var wg sync.WaitGroup

	switch o.dataType {
	case "green":
		if o.topicNameGreen == "" || o.greenDataUrl == "" {
			return fmt.Errorf("need to specify both topicNameGreen and greenDataUrl to ingest green taxi data")
		}
		ingestArgs = append(ingestArgs, ingestionArgs{
			o.greenDataUrl, types.GreenTaxiTripSchema, o.topicNameGreen, o.speed, o.maxRecordNumber, greenMessage})
		break
	case "yellow":
		if o.topicNameYellow == "" || o.yellowDataUrl == "" {
			return fmt.Errorf("need to specify both topicNameYellow and yellowDataUrl to ingest yellow taxi data")
		}
		ingestArgs = append(ingestArgs, ingestionArgs{
			o.yellowDataUrl, types.YellowTaxiTripSchema, o.topicNameYellow, o.speed, o.maxRecordNumber, yellowMessage})
		break
	case "both":
		if o.topicNameYellow == "" || o.yellowDataUrl == "" || o.topicNameGreen == "" || o.greenDataUrl == "" {
			return fmt.Errorf("need to specify topicNameYellow,yellowDataUrl,topicNameGreen and greenDataUrl to " +
				"ingest both green and yellow taxi data")
		}
		ingestArgs = append(ingestArgs, ingestionArgs{
			o.greenDataUrl, types.GreenTaxiTripSchema, o.topicNameGreen, o.speed, o.maxRecordNumber, greenMessage})
		ingestArgs = append(ingestArgs, ingestionArgs{
			o.yellowDataUrl, types.YellowTaxiTripSchema, o.topicNameYellow, o.speed, o.maxRecordNumber, yellowMessage})
		break
	default:
		return fmt.Errorf("%s not a valid dataType option, allowed values are 'green','yellow' or 'both'", o.dataType)
	}

	wg.Add(len(ingestArgs))
	for _, ingestArg := range ingestArgs {
		go o.ingestData(
			ingestArg.url,
			ingestArg.schema,
			ingestArg.topic,
			ingestArg.speed,
			ingestArg.parser,
			ingestArg.maxRecordNumber,
			&wg,
		)
	}
	wg.Wait()
	return nil
}

func (o *ingestionOps) Add(cmd *cobra.Command) {
	cmd.Flags().SortFlags = false
	cmd.Flags().StringVar(&o.pulsarUrl, "pulsarUrl", "", "Url of pulsar cluster to connect")
	_ = cmd.MarkFlagRequired("pulsarUrl")
	cmd.Flags().StringVar(&o.topicNameGreen, "topicNameGreen", "taxidata-green", "Topic to ingest green taxi data to")
	cmd.Flags().StringVar(&o.topicNameYellow, "topicNameYellow", "taxidata-yellow", "Topic to ingest yellow taxi data to")
	cmd.Flags().IntVar(&o.speed, "speed", 100, "Max speed for ingestion as number of message/second, e.g. it can't go beyond this speed but could go below it.")
	cmd.Flags().Int64Var(&o.maxRecordNumber, "maxRecordNumber", 10000, "Maximum number of message to ingest, "+
		"if not specified will ingest whole record set.")
	cmd.Flags().StringVar(&o.dataType, "dataType", "both", "Type of data to ingest, can be 'green', 'yellow' or 'both'")
	cmd.Flags().StringVar(&o.greenDataUrl, "greenDataUrl", "https://s3.amazonaws.com/nyc-tlc/trip+data/green_tripdata_2019-01.csv",
		"Url to get green taxi data")
	cmd.Flags().StringVar(&o.yellowDataUrl, "yellowDataUrl", "https://s3.amazonaws.com/nyc-tlc/trip+data/yellow_tripdata_2019-01.csv",
		"Url to get yellow taxi data")
	cmd.Flags().BoolVar(&o.verbose, "verbose", false, "Log data")
	o.AuthenticationFlags.AddFlags(cmd.Flags())
}

func (o *ingestionOps) Validate() error {
	return o.AuthenticationFlags.Validate()
}

func (o *ingestionOps) ingestData(url, schema, topic string, speed int, parser ParseFunc, maxRecordNumber int64, wg *sync.WaitGroup) {
	defer wg.Done()
	err := o.ingestToPulsar(url, schema, topic, speed, maxRecordNumber, parser)
	if err != nil {
		panic(err)
	}
	fmt.Println("Ingested: " + url)
}

func (o *ingestionOps) ingestToPulsar(url, schema, topic string, speed int, maxRecordNumber int64, parser ParseFunc) error {

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
	if o.verbose {
		fmt.Printf("Headers : %v \n", header)
	}

	clientOpts, err := o.getClientOptions()
	if err != nil {
		fmt.Println("An error encountered ::", err)
		return err
	}
	client, err := pulsar.NewClient(clientOpts)
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
		if o.verbose {
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
			produceWithRetry(o.verbose, producer, record, 3, parser)
			if o.verbose {
				fmt.Printf("Row %d : %v \n", i, record)
			}
			if i%100 == 0 {
				err = producer.Flush()
				if err != nil {
					fmt.Println(err)
				}
			}
			record, err = reader.Read()
		}
		err = producer.Flush()
		if err != nil {
			fmt.Println("An error encountered ::", err)
			return err
		}
	}
	return err
}

func produceWithRetry(verbose bool, producer pulsar.Producer, taxiRecord []string, retry int, parser ParseFunc) {
	if retry >= 0 {
		producer.SendAsync(context.Background(), &pulsar.ProducerMessage{
			Value: parser(taxiRecord),
		}, func(id pulsar.MessageID, message *pulsar.ProducerMessage, e error) {
			if e != nil {
				produceWithRetry(verbose, producer, taxiRecord, retry-1, parser)
				fmt.Printf("Got error publishing message %v with exception %s, retry left %d", message, e, retry)
			} else {
				if verbose {
					fmt.Printf("Published message %s", id)
				}
			}
		})
	}
}

func (o *ingestionOps) getClientOptions() (pulsar.ClientOptions, error) {
	pco := pulsar.ClientOptions{}
	pco.URL = o.pulsarUrl
	auth, err := o.ToAuthenticationProvider()
	if err != nil {
		return pulsar.ClientOptions{}, err
	}
	pco.Authentication = auth
	return pco, nil
}

func greenMessage(record []string) interface{} {
	return types.ParseGreenTaxiTrip(record)
}

func yellowMessage(record []string) interface{} {
	return types.ParseYellowTaxiTrip(record)
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
//	taxidata := &GreenTaxiTrip{}
//	jsonSchema.Decode(msg.Payload(), taxidata)
//	fmt.Println(taxidata)
//}
