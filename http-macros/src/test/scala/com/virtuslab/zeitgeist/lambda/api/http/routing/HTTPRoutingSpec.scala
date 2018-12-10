package com.virtuslab.zeitgeist.lambda.api.http.routing

import com.virtuslab.zeitgeist.lambda.api.LambdaSpec
import com.virtuslab.zeitgeist.lambda.api.http.http.LambdaHTTPRequest
import com.virtuslab.zeitgeist.lambda.api.http.macros.LambdaHTTPApi
import com.virtuslab.zeitgeist.lambda.api.http.macros.macros._
import com.virtuslab.zeitgeist.lambda.api.http.routing.TestRequests._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import org.json4s.{NoTypeHints, _}
import org.scalatest.{MustMatchers, WordSpec}

class HTTPRoutingSpec extends WordSpec with MustMatchers with LambdaSpec {
  protected implicit val formats = Serialization.formats(NoTypeHints)

  "Running a routing request" should {
    "allow a magnet response of an int" in {
      val input = sampleRequestPost
      val json = parse(input)

      val req = json.extract[LambdaHTTPRequest]

      val server = new TestHTTPServer
      val handler = server.newHandler
      val response = handler.routeRequest(req, null)

      response must have (
        'statusCode (200),
        'body (None)
      )
    }

    "allow a magnet response of a case class" in {
      val input = sampleRequestPut
      val json = parse(input)

      val req = json.extract[LambdaHTTPRequest]

      val server = new TestHTTPServer
      val handler = server.newHandler
      val response = handler.routeRequest(req, null)

      response must have (
        'statusCode (200),
        'body (Option(write(TestObject("OMG", "WTF"))))
      )
    }
  }

  "Slack GET request" should {
    "work well in simple scenario" in {
      val input = sampleSlackGet
      val json = parse(input)

      val req = json.extract[LambdaHTTPRequest]

      val server = new TestHTTPServer
      val handler = server.newHandler
      val response = handler.routeRequest(req, generateContext)

      response must have (
        'statusCode (200),
        'body (Option("Works"))
      )

    }
  }
}

@LambdaHTTPApi
class TestHTTPServer extends MustMatchers {

  get("/quaich-http-demo/users/{username}/foo/{bar}") { requestContext: LambdaRequestContext =>
    complete("OK")
  }

  post[String]("/version") { requestContext: LambdaRequestContext =>
    val body = requestContext.request.body.getOrElse("")
    body must include("token=xxxxxx")
    complete("Works")
  }

  post[TestObject]("/quaich-http-demo/users/{username}/foo/{bar}") { requestContext: LambdaRequestContext =>
    println(s"Post Body: ${requestContext.request.body}")
    complete(200)
  }

  put[TestObject]("/quaich-http-demo/users/{username}") { requestContext: LambdaRequestContext =>
    println(s"Username: ${requestContext.request.pathParameters("username")}")
    val response = TestObject("OMG", "WTF")
    complete(response)
  }
}

case class TestObject(foo: String, bar: String)
