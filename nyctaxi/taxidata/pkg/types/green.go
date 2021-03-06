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

package types

const GreenTaxiTripSchema = `
{
   "type":"record",
   "name":"GreenTaxiTrip",
   "namespace":"io.streamnative.pulsar.avro.taxidata",
   "doc":"This is an avro schema for NYC taxi data. Each record represents a single trip made by an NYC green taxi.",
   "fields":[
      {
         "name":"VendorID",
         "type":"int"
      },
      {
         "name":"PickUpDateTime",
         "type":"long",
         "logicalType":"timestamp-millis"
      },
      {
         "name":"DropOffDateTime",
         "type":"long",
         "logicalType":"timestamp-millis"
      },
      {
         "name":"StoreAndFwdFlag",
         "type":"boolean"
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
`

type GreenTaxiTrip struct {
	VendorID             int
	PickUpDateTime       int64
	DropOffDateTime      int64
	StoreAndFwdFlag      bool
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

func ParseGreenTaxiTrip(record []string) GreenTaxiTrip {
	return GreenTaxiTrip{
		VendorID:             getInt(record[0]),
		PickUpDateTime:       getTimestamp(record[1]),
		DropOffDateTime:      getTimestamp(record[2]),
		StoreAndFwdFlag:      getBoolean(record[3]),
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
