package org.nandemo.scala.kafka.stream

import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Source
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerRecord

object KafkaStringStream {
  implicit val serializationResolver = DefaultKafkaStringSerializationResolver

  def kafkaSource(topic: String, consumerGroupId: String)
                 (implicit config: Config): Source[ConsumerRecord[String, String], Consumer.Control] =
    KafkaStream.kafkaSource[String, String](topic, consumerGroupId)

  def kafkaSink()(implicit config: Config) =
    KafkaStream.kafkaSink()
}
