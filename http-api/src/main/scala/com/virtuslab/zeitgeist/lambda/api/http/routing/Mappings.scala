package com.virtuslab.zeitgeist.lambda.api.http.routing

import scala.collection.mutable.{Map => MutableMap}

case class Mappings(routes: MutableMap[String, HTTPRoute[_]] = MutableMap())
