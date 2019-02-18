package org.nandemo.scala.kafka.stream

import akka.Done
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.concurrent.Future

object KafkaStream {
  def kafkaSource(topic: String, consumerGroupId: String): Source[ConsumerRecord[String, String], Consumer.Control] = {
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