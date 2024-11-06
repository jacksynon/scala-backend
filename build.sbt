scalaVersion := "2.12.20"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.5.3",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.6",
  "com.typesafe.akka"  %% "akka-stream" % "2.8.6",
   "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3"
)