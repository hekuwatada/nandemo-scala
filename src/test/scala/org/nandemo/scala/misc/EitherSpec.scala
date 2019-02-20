package org.nandemo.scala.misc

import org.scalatest.{FunSpec, Matchers}

class EitherSpec extends FunSpec with Matchers {

  describe("Either right bias with scala >2.12.x") {
    it("maps over right") {
      val right: Either[Throwable, Int] = Right(1)
      right.map(_ + 2) shouldBe Right(3)
    }

    it("does not map over left") {
      val left: Either[Throwable, Int] = Left(new Throwable("error"))
      left.map(_ + 2) shouldBe left
    }
  }
}
