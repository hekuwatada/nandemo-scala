package org.nandemo.scala.actor.fsm

import akka.actor.FSM.{CurrentState, SubscribeTransitionCallBack, Transition}
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import org.nandemo.scala.actor.fsm.StackActor._
import org.scalatest.FunSpecLike

class StackActorSpec extends TestKit(ActorSystem("StackActorSpec")) with FunSpecLike {
  describe("Stock Actor") {
    it("notifies listener of state change only via FSM") {
      val actor = TestActorRef(Props(classOf[StackActor], TestProbe().ref))
      val monitor = TestProbe() //TODO: unsubscribe

      actor ! SubscribeTransitionCallBack(monitor.ref)

      monitor.expectMsg(CurrentState(actor, Idle))

      actor ! Push(1)

      monitor.expectMsg(Transition(actor, Idle, Active))

      actor ! Push(2)

      // force state transition for notification of state data change
      monitor.expectMsg(Transition(actor, Active, Active))
    }

    it("notifies monitor of state change with state data") {
      val monitor = TestProbe()
      val actor = TestActorRef(Props(classOf[StackActor], monitor.ref))

      actor ! Push(5)

      monitor.expectMsg(StateChange(Idle, Active, Stack(Seq(5))))

      actor ! Push(7)

      monitor.expectMsg(StateChange(Active, Active, Stack(Seq(5,7))))
    }
  }
}

sealed class MonitoringActor extends Actor {
  override def receive: Receive = {
    case m =>
      println(s"Received $m")
  }
}