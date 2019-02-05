package org.nandemo.scala.kafka.client

import org.apache.kafka.clients.producer.{ProducerRecord, KafkaProducer => JavaKafkaProducer, Producer => JavaProducer}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito
import org.mockito.Mockito.{reset, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}


class KafkaProducerSpec extends FunSpec
  with Matchers
  with MockitoSugar
  with BeforeAndAfter
  with ScalaFutures {

  val javaProducer = mock[JavaKafkaProducer[String, String]]
  val producer = new KafkaProducer[String, String](javaProducer)
  val record = mock[ProducerRecord[String, String]]

  before {
    reset(javaProducer, record)
  }

  describe("send()") {
    it("returns failed future on error from Java Kafka send()") {
      val expectedException = TestException("send error")
      when(javaProducer.send(any(), any())).thenThrow(expectedException)
      val resF = producer.send(record)

      whenReady(resF.failed) { e =>
        e shouldBe expectedException
      }
    }
  }
}

sealed case class TestException(message: String) extends RuntimeException(message)