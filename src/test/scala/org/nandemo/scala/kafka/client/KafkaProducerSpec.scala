package org.nandemo.scala.kafka.client

import java.util.{Properties, UUID}

import org.apache.kafka.clients.producer.{ProducerConfig, RecordMetadata}
import org.nandemo.scala.kafka.{LocalKafkaConf, LocalKafkaTestUtil}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}

class KafkaProducerSpec extends FunSpec with Matchers with ScalaFutures {

  def withKafkaProducer[K, V](producerProps: Properties, testBlock: KafkaProducer[K, V] => Any): Unit = {
    lazy val producer = KafkaProducer[K, V](producerProps)
    try {
      testBlock(producer)
    } finally {
      producer.close()
    }
  }

  //TODO: move it to it module
  describe("send() - real test") {
    it("sends a message and resolves future with meta record") {
      withKafkaProducer[String, String](LocalKafkaConf(), { producer =>
        val resultF = producer.send(LocalKafkaTestUtil.record(s"test-${UUID.randomUUID()}"))
        whenReady(resultF) { result =>
          println(result) //DEBUG
          result shouldBe a[RecordMetadata]
          withClue("record is written") { result.serializedValueSize() > 0 shouldBe true }
        }
      })
    }

    it("returns failed future on error") {
      val props = LocalKafkaConf()
      props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "0") // For causing exception from Java producer

      withKafkaProducer[String, String](props, { producer =>
        val resultF = producer.send(LocalKafkaTestUtil.record(s"test-${UUID.randomUUID()}"))
        whenReady(resultF.failed) { e =>
          e.printStackTrace() //DEBUG
          e shouldBe a[org.apache.kafka.common.errors.TimeoutException]
        }
      })
    }
  }
}