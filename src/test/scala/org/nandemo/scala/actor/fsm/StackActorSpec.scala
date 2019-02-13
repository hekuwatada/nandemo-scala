package org.nandemo.scala.actor.fsm

import akka.actor.FSM.{CurrentState, SubscribeTransitionCallBack, Transition}
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import org.nandemo.scala.actor.fsm.StackActor.{Active, Idle, Push}
import org.scalatest.FunSpecLike

class StackActorSpec extends TestKit(ActorSystem("StackActorSpec")) with FunSpecLike {
  describe("Stock Actor") {
    it("notifies listener of state change") {
      val actor = TestActorRef(Props[StackActor])
      val monitor = TestProbe() //TODO: unsubscribe

      actor ! SubscribeTransitionCallBack(monitor.ref)

      actor ! Push(1)

      monitor.expectMsg(CurrentState(actor, Idle))
      monitor.expectMsg(Transition(actor, Idle, Active))
    }
  }
}

sealed class MonitoringActor extends Actor {
  override def receive: Receive = {
    case m =>
      println(s"Received $m")
  }
}