name := "omp4j"

version := "1.3"

scalaVersion := "2.10.4"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

libraryDependencies += "org.antlr.v4" % "runtime" % "4.7" from "https://www.antlr.org/download/antlr-4.7.1-complete.jar"

scalacOptions in (Compile,doc) ++= Seq(
	"-groups",
	"-implicits",
	"-feature",
	"-doc-title", "API Reference - omp4j"
)
