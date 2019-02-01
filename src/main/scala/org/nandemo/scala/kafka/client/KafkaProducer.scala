package org.nandemo.scala.kafka.client

import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, ProducerRecord, RecordMetadata, KafkaProducer => JavaKafkaProducer, Producer => JavaProducer}

import scala.concurrent.{Future, Promise}
import scala.util.Success

trait KafkaProducerLike[K, V] {
  def send(record: ProducerRecord[K, V]): Future[RecordMetadata]
  def close(): Unit
}

/**
  * Async scala wrapper for Java Apache Kafka client
  */
class KafkaProducer[K, V](val producer: JavaProducer[K, V]) extends KafkaProducerLike[K, V] {
  override def send(record: ProducerRecord[K, V]): Future[RecordMetadata] = {
    val promise = Promise[RecordMetadata]()
    producer.send(record, ProducerCallback(promise))
    promise.future
  }

  override def close(): Unit = producer.close()
}

object KafkaProducer {
  def apply[K, V](props: Properties): KafkaProducer[K, V] = {
    val javaProducer = new JavaKafkaProducer[K, V](props)
    new KafkaProducer[K, V](javaProducer)
  }
}

case class ProducerCallback(promise: Promise[RecordMetadata]) extends Callback {
  override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
    if (exception == null) promise.complete(Success(metadata))
    else promise.failure(exception)
  }
}
