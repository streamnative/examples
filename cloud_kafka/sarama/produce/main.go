package main

import (
	"context"
	"crypto/tls"
	"flag"
	"fmt"
	"log"
	"os"
	"os/signal"
	"strings"
	"sync"
	"syscall"
	"time"

	"github.com/IBM/sarama"
)

// Sarama configuration options
var (
	brokers = ""
	apiKey  = ""
	topics  = ""
	verbose = false
)

func init() {
	flag.StringVar(&brokers, "brokers", "", "Kafka bootstrap brokers to connect to, as a comma separated list")
	flag.StringVar(&apiKey, "apiKey", "", "API key for accessing StreamNative Cloud Kafka Service")
	flag.StringVar(&topics, "topics", "", "Kafka topics to be consumed, as a comma separated list")
	flag.BoolVar(&verbose, "verbose", false, "Sarama logging")
	flag.Parse()

	if len(brokers) == 0 {
		panic("no Kafka bootstrap brokers defined, please set the -brokers flag")
	}

	if len(topics) == 0 {
		panic("no topics given to be consumed, please set the -topics flag")
	}
}

func main() {
	log.Println("Starting a new Sarama producer")

	if verbose {
		sarama.Logger = log.New(os.Stdout, "[sarama] ", log.LstdFlags)
	}

	/**
	 * Construct a new Sarama configuration.
	 * The Kafka cluster version has to be defined before the consumer/producer is initialized.
	 */
	config := sarama.NewConfig()
	config.Producer.RequiredAcks = sarama.WaitForAll
	config.Producer.Retry.Max = 5
	config.Producer.Return.Successes = true

	/**
	 * Setup SASL_SSL auth for accessing StreamNative Cloud Kafka Service
	 */
	if apiKey != "" {
		config.Net.SASL.Enable = true
		config.Net.SASL.Mechanism = sarama.SASLTypePlaintext
		config.Net.SASL.User = "user"
		config.Net.SASL.Password = "token:" + apiKey

		tlsConfig := tls.Config{}
		config.Net.TLS.Enable = true
		config.Net.TLS.Config = &tlsConfig
	}

	// Create context for graceful shutdown
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// Handle shutdown gracefully
	signals := make(chan os.Signal, 1)
	signal.Notify(signals, syscall.SIGINT, syscall.SIGTERM)

	var wg sync.WaitGroup
	wg.Add(3)

	// Start different producer examples
	go runSyncProducer(ctx, &wg, strings.Split(brokers, ","), topics, config)
	go runAsyncProducer(ctx, &wg, strings.Split(brokers, ","), topics, config)
	go runBatchProducer(ctx, &wg, strings.Split(brokers, ","), topics, config)

	<-signals
	log.Println("Initiating shutdown...")
	cancel()
	wg.Wait()
}

// Synchronous producer example
func runSyncProducer(ctx context.Context, wg *sync.WaitGroup, brokers []string, topic string, config *sarama.Config) {
	defer wg.Done()

	// Create sync producer
	producer, err := sarama.NewSyncProducer(brokers, config)
	if err != nil {
		log.Printf("Failed to create sync producer: %v", err)
		return
	}
	defer func() {
		if err := producer.Close(); err != nil {
			log.Printf("Error closing sync producer: %v", err)
		}
	}()

	// Produce messages until context is cancelled
	msgCount := 0
	for {
		select {
		case <-ctx.Done():
			return
		default:
			msg := &sarama.ProducerMessage{
				Topic: topic,
				Key:   sarama.StringEncoder(fmt.Sprintf("key-%d", msgCount)),
				Value: sarama.StringEncoder(fmt.Sprintf("message-%d", msgCount)),
				Headers: []sarama.RecordHeader{
					{
						Key:   []byte("header-key"),
						Value: []byte("header-value"),
					},
				},
			}

			partition, offset, err := producer.SendMessage(msg)
			if err != nil {
				log.Printf("Failed to send message: %v", err)
			} else {
				log.Printf("Sync message sent: partition=%d, offset=%d", partition, offset)
			}

			msgCount++
			time.Sleep(time.Second) // Simulate work
		}
	}
}

// Asynchronous producer example
func runAsyncProducer(ctx context.Context, wg *sync.WaitGroup, brokers []string, topic string, config *sarama.Config) {
	defer wg.Done()

	// Create async producer
	producer, err := sarama.NewAsyncProducer(brokers, config)
	if err != nil {
		log.Printf("Failed to create async producer: %v", err)
		return
	}

	// Handle producer success and error channels
	go func() {
		for {
			select {
			case success := <-producer.Successes():
				if success != nil {
					log.Printf("Async message success: partition=%d, offset=%d",
						success.Partition, success.Offset)
				}
			case err := <-producer.Errors():
				if err != nil {
					log.Printf("Async message error: %v", err)
				}
			case <-ctx.Done():
				return
			}
		}
	}()

	defer func() {
		if err := producer.Close(); err != nil {
			log.Printf("Error closing async producer: %v", err)
		}
	}()

	// Produce messages until context is cancelled
	msgCount := 0
	for {
		select {
		case <-ctx.Done():
			return
		default:
			msg := &sarama.ProducerMessage{
				Topic:    topic,
				Key:      sarama.StringEncoder(fmt.Sprintf("async-key-%d", msgCount)),
				Value:    sarama.StringEncoder(fmt.Sprintf("async-message-%d", msgCount)),
				Metadata: msgCount, // Optional metadata for tracking
			}

			producer.Input() <- msg
			msgCount++
			time.Sleep(time.Second) // Simulate work
		}
	}
}

// Batch producer example with custom batching logic
func runBatchProducer(ctx context.Context, wg *sync.WaitGroup, brokers []string, topic string, config *sarama.Config) {
	defer wg.Done()

	// Create sync producer for batch sending
	producer, err := sarama.NewSyncProducer(brokers, config)
	if err != nil {
		log.Printf("Failed to create batch producer: %v", err)
		return
	}
	defer func() {
		if err := producer.Close(); err != nil {
			log.Printf("Error closing batch producer: %v", err)
		}
	}()

	batchSize := 10
	ticker := time.NewTicker(5 * time.Second)
	defer ticker.Stop()

	var batch []*sarama.ProducerMessage
	msgCount := 0

	for {
		select {
		case <-ctx.Done():
			// Send remaining messages in batch
			if len(batch) > 0 {
				if err := producer.SendMessages(batch); err != nil {
					log.Printf("Failed to send final batch: %v", err)
				}
			}
			return

		case <-ticker.C:
			// Send batch on timer if there are messages
			if len(batch) > 0 {
				if err := producer.SendMessages(batch); err != nil {
					log.Printf("Failed to send batch on timer: %v", err)
				} else {
					log.Printf("Sent batch of %d messages on timer", len(batch))
				}
				batch = make([]*sarama.ProducerMessage, 0, batchSize)
			}

		default:
			// Add message to batch
			msg := &sarama.ProducerMessage{
				Topic: topic,
				Key:   sarama.StringEncoder(fmt.Sprintf("batch-key-%d", msgCount)),
				Value: sarama.StringEncoder(fmt.Sprintf("batch-message-%d", msgCount)),
			}
			batch = append(batch, msg)
			msgCount++

			// Send batch if it reaches batch size
			if len(batch) >= batchSize {
				if err := producer.SendMessages(batch); err != nil {
					log.Printf("Failed to send batch: %v", err)
				} else {
					log.Printf("Sent batch of %d messages", len(batch))
				}
				batch = make([]*sarama.ProducerMessage, 0, batchSize)
			}

			time.Sleep(500 * time.Millisecond) // Simulate message generation
		}
	}
}
