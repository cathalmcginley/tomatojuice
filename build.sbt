organization := "org.gnostai"

name := "tomatojuice"

version := "0.0.1"

scalaVersion := "2.11.1"

resolvers += "Big Bee Consultants" at "http://repo.bigbeeconsultants.co.uk/repo"

libraryDependencies ++= List(
  "com.typesafe.akka" % "akka-actor_2.11"   % "2.3.3",
  "com.typesafe.akka" % "akka-slf4j_2.11"   % "2.3.3",
  "com.typesafe.akka" % "akka-remote_2.11"  % "2.3.3",
  "com.typesafe.akka" % "akka-agent_2.11"   % "2.3.3",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.3",
  "ch.qos.logback" % "logback-classic" % "1.0.9",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.3" % "test",
  "mysql" % "mysql-connector-java" % "5.1.31",
  "com.jolbox" % "bonecp" % "0.8.0.RELEASE",
  "org.jcraft" % "jorbis" % "0.0.17"
)

excludeFilter in unmanagedJars in Compile := "libgtkjni-4.1.3.so"

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
