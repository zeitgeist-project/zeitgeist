logLevel := Level.Warn

resolvers += "Cloudsmith Snapshots" at "https://dl.cloudsmith.io/public/zeitgeist-project/snapshots/maven"
resolvers += "Cloudsmith Releases" at "https://dl.cloudsmith.io/public/zeitgeist-project/releases/maven"

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.8.2")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.3.0")

val zeitgeistSbtVersion = "0.1.3-SNAPSHOT"

addSbtPlugin("com.virtuslab.zeitgeist" % "sbt-lambda" % zeitgeistSbtVersion)
addSbtPlugin("com.virtuslab.zeitgeist" % "sbt-api-gateway" % zeitgeistSbtVersion)
