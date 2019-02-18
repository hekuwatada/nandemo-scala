package org.nandemo.scala.kafka.stream

import org.apache.kafka.common.serialization.{Deserializer, Serializer, StringDeserializer, StringSerializer}

trait KafkaSerializationResolver[A] {
  def serializer: Serializer[A]
  def deserializer: Deserializer[A]
}

object DefaultKafkaStringSerializationResolver extends KafkaSerializationResolver[String] {
  override def serializer: Serializer[String] = new StringSerializer()
  override def deserializer: Deserializer[String] = new StringDeserializer()
}