package org.nandemo.scala.stream

import akka.Done
import akka.actor.Actor
import akka.stream.scaladsl.SourceQueueWithComplete
import org.nandemo.scala.stream.message.TestMessage

class WriteActor(queue: SourceQueueWithComplete[TestMessage]) extends Actor {
  override def receive: Receive = {
    case m: TestMessage =>
      queue.offer(m)
      sender() ! Done //?
  }
}
