package org.nandemo.scala.stream.actor

import java.util.UUID

import akka.Done
import akka.actor.{ActorSystem, Props}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Sink, Source, SourceQueueWithComplete}
import akka.util.Timeout
import org.nandemo.scala.stream.{ReadActor, WriteActor}
import org.nandemo.scala.stream.message.TestMessage
import akka.pattern.ask

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object StreamToActor extends App {

  implicit val system = ActorSystem("stream-to-actor")
  implicit val materializer = ActorMaterializer()
  implicit val askTimeout = Timeout(5 seconds)

  val readActor = system.actorOf(Props[ReadActor])

  val f: Future[Done] = Source(1 to 10)
    .map(i => TestMessage(s"$i-${UUID.randomUUID()}"))
    .ask[Done](parallelism = 2)(readActor)
    .runWith(Sink.ignore)
}

object ActorToStream extends App {

  implicit val system = ActorSystem("actor-to-stream")
  implicit val materializer = ActorMaterializer()

  val queue: SourceQueueWithComplete[TestMessage] =
    Source.queue[TestMessage](bufferSize = 1000, OverflowStrategy.backpressure)
      .to(Sink.foreach(println)) //?
      .run()

  val writeActor = system.actorOf(Props(classOf[WriteActor], queue))

  writeActor ! TestMessage(s"toactor-${UUID.randomUUID()}")
}

