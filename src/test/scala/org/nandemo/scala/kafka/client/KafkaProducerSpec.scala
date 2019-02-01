package org.nandemo.scala.kafka.client

import java.util.{Properties, UUID}

import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord, RecordMetadata}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}

class KafkaProducerSpec extends FunSpec with Matchers with BeforeAndAfterAll with ScalaFutures {

  lazy val producer = KafkaProducer[String, String](LocalKafkaConf())

  override def afterAll() = {
    producer.close()
  }

  //TODO: move it to it module
  describe("send() - real test") {
    it("sends a message and resolves future with meta record") {
      val resultF = producer.send(LocalKafkaTestUtil.record(s"test-${UUID.randomUUID()}"))
      whenReady(resultF) { result =>
        println(result) //DEBUG
        result shouldBe a[RecordMetadata]
      }
    }

    it("returns failed future on error") {
      (pending)
    }
  }
}

object LocalKafkaTestUtil {
  def record(message: String): ProducerRecord[String, String] =
    new ProducerRecord[String, String](LocalKafkaConf.testTopic, "key", message)
}

object LocalKafkaConf {
  val LocalKafkaServer = "localhost:9092"
  val StringSerializer = "org.apache.kafka.common.serialization.StringSerializer"
  val testTopic = "t1"

  def apply(): Properties = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, LocalKafkaServer)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer)
//    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "0") // For causing exception from Java producer
    props
  }
}