package org.nandemo.scala.sys

object ShutdownHookExample extends App {
  println("a")

  //TODO: a better example to illustrate shutdown hook
  sys.addShutdownHook(() =>
    println("shutdown hook")
  )

  println("b")
}
