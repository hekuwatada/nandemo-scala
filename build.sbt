import sbt.Keys.libraryDependencies

name := "nandemo-scala"

version := "0.1"

scalaVersion := "2.12.8"

val prodLibs = Seq(
  "org.apache.kafka" % "kafka-clients" % "0.10.2.2"
)

val testLibs = Seq(
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

libraryDependencies ++= prodLibs ++ testLibs




