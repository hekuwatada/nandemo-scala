package org.nandemo.scala.actor.fsm

import akka.actor.{ActorRef, FSM}
import org.nandemo.scala.actor.fsm.StackActor.StateData

/**
  * Simple example of FSM actor
  */
object StackActor {
  sealed trait State
  case object Idle extends State
  case object Active extends State

  sealed trait StateData
  case object Empty extends StateData
  final case class Stack(values: Seq[Int]) extends StateData

  sealed trait StackEvent
  final case class Push(value: Int) extends StackEvent
  case object Pop extends StackEvent
  case object Flush extends StackEvent

  sealed trait StateChangeEvent[S, SD]
  final case class StateChange[S, SD](oldState: S, newState: S, newStateData: SD) extends StateChangeEvent[S, SD]
}

class StackActor(stateDataMonitor: ActorRef) extends FSM[StackActor.State, StateData] {
  import StackActor._

  startWith(Idle, Empty)

  when(Idle) {
    case Event(Push(v: Int), Empty) =>
      goto(Active) using Stack(Seq(v))
    case Event(Flush, _) => stay
  }

  when(Active) {
    case Event(Push(v: Int), stack: Stack) =>
      goto(Active) using Stack(stack.values ++ Seq(v))
    case Event(Pop, stack: Stack) if 1 < stack.values.size =>
      goto(Active) using Stack(stack.values.tail)
    case Event(Pop, stack: Stack) if !(1 < stack.values.size) =>
      goto(Idle) using Empty
    case Event(Flush, _) =>
      goto(Idle) using Empty
  }

  onTransition {
    case oldState -> nextState =>
      stateDataMonitor ! StateChange(oldState, nextState, nextStateData)
  }

  whenUnhandled {
    case m =>
      println(s"Received unknown message: $m")
      stay
  }
}
