import sbt.Keys.{libraryDependencies, publishTo}

name := "zeitgeist-lambda"

val projectVersion        = "0.1.0-SNAPSHOT"
val projectOrg            = "com.virtuslab.zeitgeist"

val awsLambdaVersion      = "1.2.0"
val awsLambdaEventsVer    = "2.2.4"
val awsSdkVersion         = "1.11.1034"

lazy val commonSettings = Seq(
  organization := projectOrg,
  version := projectVersion,
  scalaVersion := "2.12.8",
  retrieveManaged := true,

  libraryDependencies ++= Seq(
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.7",
    "commons-io" % "commons-io" % "2.4",

    "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
    "org.scalatest" %% "scalatest" % "3.0.0" % Test
  ),
  scalacOptions := Seq(
    "-encoding",
    "UTF-8",
    "-target:jvm-1.8",
    "-deprecation",
    "-language:_"/*,
    "-Ymacro-debug-lite"*/
  ),
  Test / run / fork := true,

  licenses += "MIT" -> url("http://opensource.org/licenses/MIT"),

  credentials ++= {
    val credentialsFile = Path.userHome / ".sbt" / ".credentials.zeitgeist"
    if (credentialsFile.exists())
      Seq(Credentials(credentialsFile))
    else if (sys.env.contains("CLOUDSMITH_USERNAME") && sys.env.contains("CLOUDSMITH_PASSWORD"))
      Seq(Credentials("Cloudsmith API", "maven.cloudsmith.io", sys.env("CLOUDSMITH_USERNAME"), sys.env("CLOUDSMITH_PASSWORD")))
    else
      Nil
  },

  publishTo := {
    val repo = "https://maven.cloudsmith.io/zeitgeist-project/"
    if (isSnapshot.value)
      Some("Cloudsmith Snapshots" at repo + "snapshots")
    else
      Some("Cloudsmith Releases" at repo + "releases")
  }
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "lambda",
    organization := projectOrg,
    version := projectVersion,
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))),
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
      "com.amazonaws" % "aws-lambda-java-log4j2" % "1.1.0"
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

    Compile / packageDoc / publishArtifact := false,
  ).
  dependsOn(httpMacros, httpApi).
  enablePlugins(AWSLambdaPlugin, AwsApiGatewayPlugin)


assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}


