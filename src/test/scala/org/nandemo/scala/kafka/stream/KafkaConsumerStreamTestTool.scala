package org.nandemo.scala.kafka.stream

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.nandemo.scala.kafka.LocalKafkaConf

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object KafkaConsumerStreamTestTool extends App {

  implicit val system = ActorSystem("kafka-consumer-stream")
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val groupId = "group1"

  val consumerSettings =
    ConsumerSettings(config.getConfig("akka.kafka.consumer"), new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(LocalKafkaConf.LocalKafkaServer)
    .withGroupId(groupId)

  val source: Source[ConsumerRecord[String, String], Consumer.Control] = Consumer
    .plainSource[String, String](consumerSettings, Subscriptions.topics(LocalKafkaConf.TestTopic))

  val doneF: Future[Done] = source
      .runForeach((record: ConsumerRecord[String, String]) => println(record.value()))

  Await.result(doneF, 30 seconds)
}
