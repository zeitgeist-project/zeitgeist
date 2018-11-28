import sbt.Keys.{libraryDependencies, publishTo}

name := "lambda"

val projectVersion        = "0.0.1-SNAPSHOT"
val projectOrg            = "cloud.zeitgeist"

val awsLambdaVersion      = "1.1.0"
val awsLambdaEventsVer    = "1.3.0"
val awsLambdaLog4jVer     = "1.0.0"
val metaParadiseVersion   = "3.0.0-M11"
val awsSdkVersion         = "1.11.52"

lazy val commonSettings = Seq(
  organization := projectOrg,
  version := projectVersion,
  scalaVersion := "2.12.7",
  retrieveManaged := true,

  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % "3.0.0",
    "org.json4s" %% "json4s-jackson" % "3.5.0.RC1",
    "commons-io" % "commons-io" % "2.4",

    "ch.qos.logback" % "logback-classic" % "1.1.7" % Test,
    "org.scalatest" %% "scalatest" % "3.0.0" % Test,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0" % Test
  ),
  scalacOptions := Seq(
    "-encoding",
    "UTF-8",
    "-target:jvm-1.8",
    "-deprecation",
    "-language:_"/*,
    "-Ymacro-debug-lite"*/
  ),
  fork in (Test, run) := true,

  bintrayOrganization := Some("zeitgeist"),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "lambda",
    organization := projectOrg,
    version := projectVersion,
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))),
    bintrayRelease := {}
  ).
  aggregate(
    util, httpMacros, httpApi, api, demo
  )

lazy val util = (project in file("util")).
  settings(commonSettings: _*).
  settings(
    name := "util",
    libraryDependencies ++= Seq(
    )
  )

lazy val api = (project in file("api")).
  settings(commonSettings: _*).
  settings(
    name := "api",
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-core" % awsLambdaVersion,
      "com.amazonaws" % "aws-lambda-java-events" % awsLambdaEventsVer,
      "com.amazonaws" % "aws-lambda-java-log4j" % awsLambdaLog4jVer
    )
  ).dependsOn(util)

lazy val httpApi = (project in file("http-api")).
  settings(commonSettings: _*).
  settings(
    name := "http-api",
    libraryDependencies ++= Seq(
    )
  ).dependsOn(api)

lazy val httpMacros = (project in file("http-macros")).
  settings(commonSettings: _*).
  settings(zeitgeistMacroSettings: _*).
  settings(
    name := "http-macros"
  ).
  dependsOn(httpApi, api, api % "test->test")

lazy val demo = (project in file("demo")).
  settings(commonSettings: _*).
  settings(zeitgeistMacroSettings: _*).
  settings(
    name := "demo",
    createAutomatically := true,

    lambdaName := "zeitgeist-http-demo",
    awsLambdaMemory := 192,
    awsLambdaTimeout := 30,
    region := "eu-west-1",

    publishArtifact in (Compile, packageDoc) := false
  ).
  dependsOn(httpMacros, httpApi).
  enablePlugins(AWSLambdaPlugin, AwsApiGatewayPlugin)


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}


