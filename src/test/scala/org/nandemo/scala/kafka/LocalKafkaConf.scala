package org.nandemo.scala.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord}

object LocalKafkaTestUtil {
  def record(message: String): ProducerRecord[String, String] =
    new ProducerRecord[String, String](LocalKafkaConf.TestTopic, "key", message)
}

object LocalKafkaConf {
  val LocalKafkaServer = "localhost:9092"
  val StringSerializer = "org.apache.kafka.common.serialization.StringSerializer"
  val TestTopic = "t1"

  def apply(): Properties = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, LocalKafkaServer)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer)
    //    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "0") // For causing exception from Java producer
    props
  }
}