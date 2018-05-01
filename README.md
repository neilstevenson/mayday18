# mayday18
Spring Boot tester...

# Instructions

## Kafka
You'll need Kafka installed, from [Apache Kafka](https://kafka.apache.org/quickstart)

The scripts assume a Mac and `/Applications/kafka_2.11-1.0.0`

Later versions, such as 1.1.0 haven't been tried but should be ok.

## In `src/main/scripts`

Run `start-zookeeper.sh` to start one Zookeeper config server

Run `start-kafka-0.sh`, `start-kafka-1.sh` and `start-kafka-2.sh` to start three Kafka brokers

Run `print-topic.sh` to create a partitioned (into 3) Kafka topic, and to listen for messages being written to the topic

Run `print-topic-2.sh` to create another partitioned (into 3) Kafka topic, and to listen for messages being written to the topic, and keep this running.

## Populate Kafka

Run 

```
java -jar mayday2018-kafka/target/mayday2018-kafka-0.1-SNAPSHOT.jar
```

This'll write some JSON messages to the Kafka topic, and you should see them if `print-topic.sh` is still running

## Hazelcast server 

Run

```
java -jar mayday2018-hazelcast/target/mayday2018-hazelcast-0.1-SNAPSHOT.jar
```

to start a grid node

One is enough, but three is separate windows is maybe a better idea

## Hazelcast server's CLI

In any Hazelcast node's window, type "*help*" in the CLI to see what commands are available

There are 8 commands added

* `JOBS`

Lists which Jet jobs are running

* `LIST`

Shows the IMap content

* `INGEST1_START` & `INGEST1_STOP`

Starts and stop variation 1 of the ingest job (Kafka -> Imap)

* `INGEST2_START` & `INGEST2_STOP`

Starts and stop variation 1 of the ingest job (Kafka -> Imap)

* `EGEST1_START` & `EGEST1_STOP`

Starts ands stops the job that reads from IMap and writes to the screen

* `EGEST2_START` & `EGEST2_STOP`

Starts ands stops the job that reads from IMap and writes to a Kafka topic
