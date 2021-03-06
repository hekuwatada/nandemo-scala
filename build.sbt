import sbt.Keys.libraryDependencies

name := "nandemo-scala"

version := "0.1"

scalaVersion := "2.12.8"

val prodLibs = Seq(
  "org.apache.kafka" % "kafka-clients" % "2.1.0",
  "com.typesafe.akka" %% "akka-stream" % "2.5.20",
  "com.typesafe.akka" %% "akka-actor" % "2.5.20",
  "com.typesafe.akka" %% "akka-stream-kafka" % "1.0-RC1",
  "com.softwaremill.quicklens" %% "quicklens" % "1.4.11"
)

val TestIntegrationTest = "test,it"
val testLibs = Seq(
  "org.scalactic" %% "scalactic" % "3.0.5" % TestIntegrationTest,
  "org.scalatest" %% "scalatest" % "3.0.5" % TestIntegrationTest,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.20" % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.5.20" % Test,
  "org.mockito" % "mockito-core" % "2.23.4" % Test
)

//@see https://www.scala-sbt.org/1.x/docs/Testing.html
lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    libraryDependencies ++= prodLibs ++ testLibs
  )