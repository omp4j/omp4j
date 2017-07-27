name := "omp4j"

version := "1.2"

scalaVersion := "2.10.3"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

libraryDependencies += "org.antlr.v4" % "runtime" % "4.4" from "http://www.antlr.org/download/antlr-runtime-4.4.jar"

scalacOptions in (Compile,doc) ++= Seq(
	"-groups",
	"-implicits",
	"-feature",
	"-doc-title", "API Reference - omp4j"
)
