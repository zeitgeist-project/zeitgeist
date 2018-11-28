package cloud.zeitgeist.lambda.demo.http

import cloud.zeitgeist.lambda.api.http.macros.LambdaHTTPApi
import cloud.zeitgeist.lambda.demo.http.model.TestObject
import cloud.zeitgeist.lambda.api.http.macros._

import scala.util.Random


@LambdaHTTPApi
class DemoHTTPServer {
  val random = Random.nextInt()

  get("/hello") { requestContext =>
    complete("Awesome. First small success! Version: 0.0.4")
  }

  get("/users/{username}/foo/{bar}") { requestContext =>
    complete("OK")
  }

  head("/users/{username}/foo/{bar}") { requestContext =>
    complete(s"Params are: ${requestContext.request.pathParameters}")
  }

  put[TestObject]("/users/{username}/foo/{bar}") { requestWithBody =>
    println(s"Put Body: ${requestWithBody.request.body} Path Parameters: ${requestWithBody.request.pathParameters}")
    val response = TestObject("OMG", "WTF")
    complete(response)
  }

  patch[TestObject]("/users/{username}/foo/{bar}") { requestContext =>
    println(s"Patch request: $requestContext")
    complete("OK")
  }
}