name := "omp4j"

version := "0.1"

scalaVersion := "2.10.3"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits")
