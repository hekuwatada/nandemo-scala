package org.nandemo.scala.kafka.stream

import java.util.UUID

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

// Kafka Producer as a Sink
//@see https://doc.akka.io/docs/akka-stream-kafka/0.15/producer.html#producer-as-a-sink
object KafkaProducerStreamTestTool extends App {

  val config = ConfigFactory.load()
  val producerSettings = ProducerSettings(config.getConfig("akka.kafka.producer"),
    new StringSerializer,
    new StringSerializer
  )
  val kafkaProducerSink = Producer.plainSink[String, String](producerSettings)

  implicit val system = ActorSystem("kafka-producer-stream-test-tool")
  implicit val materializer = ActorMaterializer()

  Source(1 to 10)
    .map(i => s"m-$i-${UUID.randomUUID()}")
    .map(m => new ProducerRecord[String, String]("t1", "key", m))
    .to(kafkaProducerSink)
    .run()
}