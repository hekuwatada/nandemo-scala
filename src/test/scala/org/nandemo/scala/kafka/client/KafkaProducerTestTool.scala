package org.nandemo.scala.kafka.client

import java.util.UUID

import org.nandemo.scala.kafka.{LocalKafkaConf, LocalKafkaTestUtil}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

/*
  To read message:
  ./kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic t1 --from-beginning
 */
object KafkaProducerTestTool extends App {
  val producer = KafkaProducer[String, String](LocalKafkaConf())

  val message = s"test-${UUID.randomUUID().toString}"
  implicit val ec = ExecutionContext.global // ec only required for .recover
  val resultF = producer.send(LocalKafkaTestUtil.record(message))
    .recover { case e: Throwable =>
      // Set max metadata block time to 0 to cause exception
      println(">>> future failed")
      println(e.getMessage)
    }
  val result = Await.result(resultF, 30 seconds)
  println(result)

  producer.close()
}
