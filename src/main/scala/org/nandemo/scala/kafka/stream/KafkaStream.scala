package org.nandemo.scala.kafka.stream

import akka.Done
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.Future

object KafkaStream {
  def kafkaSource[K, V](topic: String, consumerGroupId: String)
                       (implicit config: Config,
                        keyDeserializerResolver: KafkaSerializationResolver[K],
                        valueDeserializerResolver: KafkaSerializationResolver[V]
                       ): Source[ConsumerRecord[K, V], Consumer.Control] = {
    val consumerSettings =
      ConsumerSettings(config.getConfig("akka.kafka.consumer"),
        keyDeserializerResolver.deserializer,
        valueDeserializerResolver.deserializer
      ).withGroupId(consumerGroupId)

    Consumer.plainSource[K, V](consumerSettings,
      Subscriptions.topics(topic))
  }

  def kafkaSink[K, V]()
               (implicit config: Config,
                keySerializerResolver: KafkaSerializationResolver[K],
                valueSerializerResolver: KafkaSerializationResolver[V]
               ): Sink[ProducerRecord[K, V], Future[Done]] = {
    val producerSettings =
      ProducerSettings(config.getConfig("akka.kafka.producer"),
        keySerializerResolver.serializer,
        valueSerializerResolver.serializer)

    Producer.plainSink[K, V](producerSettings)
  }
}