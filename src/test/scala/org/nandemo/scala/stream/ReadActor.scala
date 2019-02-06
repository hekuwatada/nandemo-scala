package org.nandemo.scala.stream

import akka.Done
import akka.actor.Actor
import org.nandemo.scala.stream.message.TestMessage

class ReadActor extends Actor {
  override def receive: Receive = {
    case m:TestMessage =>
      println(s"received $m")
      sender() ! Done //TODO: is there any pre-defined ack?
  }
}
