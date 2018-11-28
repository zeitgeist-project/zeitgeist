/*
 * Copyright (c) 2016 Brendan McAdams & Thomas Lockney
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cloud.zeitgeist.lambda.api.http.routing

import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import org.json4s.{NoTypeHints, _}
import org.scalatest.{MustMatchers, WordSpec}
import TestRequests._
import cloud.zeitgeist.lambda.api.LambdaSpec
import cloud.zeitgeist.lambda.api.http.http.{LambdaHTTPRequest, LambdaRequestContext}
import cloud.zeitgeist.lambda.api.http.macros.LambdaHTTPApi

import cloud.zeitgeist.lambda.api.http.macros._

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
