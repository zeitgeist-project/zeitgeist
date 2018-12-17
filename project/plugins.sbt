logLevel := Level.Warn

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

resolvers += Resolver.url("zeitgeist", url("https://dl.bintray.com/zeitgeist/sbt-plugins"))(Resolver.ivyStylePatterns)

resolvers += Resolver.jcenterRepo

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.4")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.7")

val zeitgeistSbtVersion = "0.0.5"

addSbtPlugin("com.virtuslab.zeitgeist" % "sbt-lambda" % zeitgeistSbtVersion)
addSbtPlugin("com.virtuslab.zeitgeist" % "sbt-api-gateway" % zeitgeistSbtVersion)