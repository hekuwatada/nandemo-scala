package org.nandemo.scala.stream.actor

import java.util.UUID

import akka.Done
import akka.actor.{ActorSystem, Props}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Sink, Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.nandemo.scala.stream.message.TestMessage
import org.nandemo.scala.stream.{ReadActor, WriteActor}

import scala.concurrent.Future
import scala.concurrent.duration._

object KafkaStream {
  val topic = "t1"
  val consumerGroupId = "group1"

  def kafkaSource(): Source[ConsumerRecord[String, String], Consumer.Control] = {
    val config = ConfigFactory.load()
    val consumerSettings =
      ConsumerSettings(config.getConfig("akka.kafka.consumer"),
        new StringDeserializer,
        new StringDeserializer
      ).withGroupId(consumerGroupId)
    Consumer.plainSource[String, String](consumerSettings,
      Subscriptions.topics(topic))
  }

  def kafkaSink(): Sink[ProducerRecord[String, String], Future[Done]] = {
    val config = ConfigFactory.load()
    val producerSettings =
      ProducerSettings(config.getConfig("akka.kafka.producer"),
        new StringSerializer,
        new StringSerializer)
    Producer.plainSink[String, String](producerSettings)
  }
}

object KafkaStreamToActor extends App {

  implicit val system = ActorSystem("stream-to-actor")
  implicit val materializer = ActorMaterializer()
  implicit val askTimeout = Timeout(5 seconds)

  val readActor = system.actorOf(Props[ReadActor])

  val f: Future[Done] = KafkaStream.kafkaSource()
    .map(r => TestMessage(r.value()))
    .ask[Done](parallelism = 2)(readActor)
    .runWith(Sink.ignore)
}

object ActorToKafkaStream extends App {

  implicit val system = ActorSystem("actor-to-stream")
  implicit val materializer = ActorMaterializer()

  val queue: SourceQueueWithComplete[TestMessage] =
    Source.queue[TestMessage](bufferSize = 1000, OverflowStrategy.backpressure)
      .map(m => new ProducerRecord[String, String](KafkaStream.topic, "key", m.value))
      .to(KafkaStream.kafkaSink()) //?
      .run()

  val writeActor = system.actorOf(Props(classOf[WriteActor], queue))

  writeActor ! TestMessage(s"ktoactor-${UUID.randomUUID()}")
}

