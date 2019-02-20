package org.nandemo.scala.misc

import org.scalatest.{FunSpec, Matchers}

class AndThenSpec extends FunSpec with Matchers {
  val f: Int => Int = (x: Int) => x + 2
  val g: Int => Int = (x: Int) => x * 2

  describe("f andThen g") {
    it("calls f then g") {
      val fg: Int => Int = f andThen g

      fg(1) shouldBe 6
      fg(2) shouldBe 8
    }
  }

  describe("f compose g") {
    it("calls g then f") {
      val gAfterF: Int => Int = f compose g

      gAfterF(1) shouldBe 4
      gAfterF(2) shouldBe 6
    }
  }
}
