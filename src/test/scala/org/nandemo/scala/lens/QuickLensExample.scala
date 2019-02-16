package org.nandemo.scala.lens

object QuickLensExample extends App {
  val address = Address("A12CD3", "42 Tree Street")
  val user = User("name", 15, address)

  import com.softwaremill.quicklens._

  val updatedUser = user
    .modify(_.address.postCode).setTo("QW9ER8")
    .modify(_.age).using(_ + 1)

  println(updatedUser)
}

case class Address(postCode: String, addressLine: String)
case class User(name: String, age: Int, address: Address)