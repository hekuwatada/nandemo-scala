package org.nandemo.scala.stream.actor

import java.util.UUID

import akka.Done
import akka.actor.{ActorSystem, Props}
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, KillSwitches, OverflowStrategy, UniqueKillSwitch}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.nandemo.scala.kafka.stream.{KafkaStream, KafkaStringStream}
import org.nandemo.scala.stream.message.TestMessage
import org.nandemo.scala.stream.{ReadActor, WriteActor}

import scala.concurrent.Future
import scala.concurrent.duration._

object KafkaStreamToActor extends App {

  val topic = "t1"
  val consumerGroupId = "group1"

  implicit val config = ConfigFactory.load()
  implicit val system = ActorSystem("stream-to-actor")
  implicit val materializer = ActorMaterializer()
  implicit val askTimeout = Timeout(5 seconds)

  val readActor = system.actorOf(Props[ReadActor])

  val f: Future[Done] = KafkaStringStream.kafkaSource(topic, consumerGroupId)
    .map(r => TestMessage(r.value()))
    .ask[Done](parallelism = 2)(readActor)
    .runWith(Sink.ignore)
}

object ActorToKafkaStream extends App {
  val topic = "t1"

  implicit val config = ConfigFactory.load()
  implicit val system = ActorSystem("actor-to-stream")
  implicit val materializer = ActorMaterializer()

  //TODO: via vs viaMat
  val runnable: RunnableGraph[(SourceQueueWithComplete[TestMessage], UniqueKillSwitch)] =
    Source.queue[TestMessage](bufferSize = 1000, OverflowStrategy.backpressure)
      .viaMat(KillSwitches.single)(Keep.both)
      .map(m => new ProducerRecord[String, String](topic, "key", m.value))
      .to(KafkaStringStream.kafkaSink())

  val (queue, killSwitch): (SourceQueueWithComplete[TestMessage], UniqueKillSwitch) = runnable.run()

  val writeActor = system.actorOf(Props(classOf[WriteActor], queue))

  writeActor ! TestMessage(s"ktoa-${UUID.randomUUID()}")

  //TODO: how to gracefully terminate actor
}

