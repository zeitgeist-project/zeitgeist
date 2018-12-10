package com.virtuslab.zeitgeist.lambda.api.http.routing

import com.virtuslab.zeitgeist.lambda.api.http.LambdaHTTPResponse
import com.virtuslab.zeitgeist.lambda.api.http.http.{CognitoData, LambdaHTTPRequest, LambdaHTTPRequestContext, LambdaRequestContext}
import org.apache.logging.log4j.scala.Logging
import org.scalatest.{MustMatchers, WordSpec}

class PathRoutingResolverSpec extends WordSpec with MustMatchers with Logging {
  implicit val log = logger

  "Path routing for simple paths" should {

    val pathResolver = new PathRoutingResolver()
    pathResolver
      .bindPath("/path", MethodRoute("GET", fabricateGetRoute("simplePath")))

    "resolving simple existing path" in {
      val req = fabricateRequest("/{path+}", "/path", "GET")

      val Some(RouteRequest(route, request)) = pathResolver.resolveRequestRoute(req)

      route(LambdaRequestContext(request, null)).body must be(Some("simplePath"))
      request.pathParameters must be(Map.empty)
    }

    "resolving not existing simple path" in {
      val req = fabricateRequest("/{path+}", "/path/onion", "GET")

      val result = pathResolver.resolveRequestRoute(req)

      result must be(empty)
    }

    "resolving existing path but incorrect method type" in {
      val req = fabricateRequest("/{path+}", "/path/onion", "PUT")

      val result = pathResolver.resolveRequestRoute(req)

      result must be(empty)
    }

    "two simple mappings for GET" in {
      val resolver = new PathRoutingResolver()
      resolver
        .bindPath("/hello", MethodRoute("GET", fabricateGetRoute("simplePath1")))
        .bindPath("/another", MethodRoute("GET", fabricateGetRoute("simplePath2")))

      val req = fabricateRequest("/{path+}", "/hello", "GET")

      val Some(RouteRequest(route, request)) = resolver.resolveRequestRoute(req)

      route(LambdaRequestContext(request, null)).body must be(Some("simplePath1"))
      request.pathParameters must be(Map.empty)
    }
  }

  "Path with parameters (dynamic)" should {
    val pathResolver = new PathRoutingResolver()
    pathResolver
      .bindPath("/path/{username}", MethodRoute("POST", fabricateGetRoute("pathParam")))
      .bindPath("/another/{name}/{surname}", MethodRoute("PUT", fabricateGetRoute("twoParams")))
      .bindPath("/path/{username}/{another}/yet/{more}", MethodRoute("GET", fabricateGetRoute("roadParam")))

    "be retrievable in simple scenario" in {
      val req = fabricateRequest("/{path+}", "/path/takeshi", "POST")
      val Some(RouteRequest(route, modifiedReq)) = pathResolver.resolveRequestRoute(req)
      route(LambdaRequestContext(modifiedReq, null)).body must be(Some("pathParam"))
      modifiedReq.pathParameters must be(Map("username" -> "takeshi"))

      val reqAnother = fabricateRequest("/{path+}", "/path/cormac", "POST")
      val Some(RouteRequest(_, anotherModifiedReq)) = pathResolver.resolveRequestRoute(reqAnother)
      anotherModifiedReq.pathParameters must be(Map("username" -> "cormac"))
    }

    "work for longer path with many dynamic params" in {
      val req = fabricateRequest("/{path+}", "/path/param1/param2/yet/param3", "GET")
      val Some(RouteRequest(route, modifiedReq)) = pathResolver.resolveRequestRoute(req)
      route(LambdaRequestContext(modifiedReq, null)).body must be(Some("roadParam"))
      modifiedReq.pathParameters must be(
        Map(
          "username" -> "param1",
          "another" -> "param2",
          "more" -> "param3"
        )
      )
    }

    "handle correctly non-existing paths" in {
      val req = fabricateRequest("/{path+}", "/another/one/two/more", "PUT")
      val result = pathResolver.resolveRequestRoute(req)
      result must be(empty)
    }

    "handle correctly existing path with wrong method" in {
      val req = fabricateRequest("/{path+}", "/path/{username}", "DELETE")
      val result = pathResolver.resolveRequestRoute(req)
      result must be(empty)
    }
  }

  "Binding" should {
    "succeed if double bind attempted for different methods" in {
      val pathResolver = new PathRoutingResolver()
        .bindPath("/path", MethodRoute("GET", fabricateGetRoute("getPath")))
        .bindPath("/path", MethodRoute("PUT", fabricateGetRoute("putPath")))

      val getReq = fabricateRequest("/{path+}", "/path", "GET")
      val Some(RouteRequest(getRoute, modGetReq)) = pathResolver.resolveRequestRoute(getReq)
      getRoute(LambdaRequestContext(modGetReq, null)).body must be(Some("getPath"))

      val putReq = fabricateRequest("/{path+}", "/path", "PUT")
      val Some(RouteRequest(putRoute, modPutReq)) = pathResolver.resolveRequestRoute(putReq)
      putRoute(LambdaRequestContext(modPutReq, null)).body must be(Some("putPath"))
    }

    "fail if double bind attempted" in {
      val pathResolver = new PathRoutingResolver()
      pathResolver
        .bindPath("/path", MethodRoute("GET", fabricateGetRoute("simplePath")))

      assertThrows[IllegalArgumentException] {
        pathResolver.bindPath("/path", MethodRoute("GET", fabricateGetRoute("simplePath")))
      }
    }

    "fail if multiple dynamic paths in same location are bound" in {
      val pathResolver = new PathRoutingResolver()
      pathResolver
        .bindPath("/path/{param}/more", MethodRoute("GET", fabricateGetRoute("simplePath")))

      assertThrows[IllegalArgumentException] {
        pathResolver.bindPath("/path/{anotherParam}/more", MethodRoute("GET", fabricateGetRoute("simplePath")))
      }

      logger.info("Binding to the same method is not possible, but another HTTP method should be fine")
      pathResolver.bindPath("/path/{anotherParam}/more", MethodRoute("POST", fabricateGetRoute("simplePath")))
    }
  }

  def fabricateRequest(resource: String, path: String, method: String) = {
    LambdaHTTPRequest(resource, path, method,
      LambdaHTTPRequestContext("xxxx", "xxxx", "dev", "xxxx",
        CognitoData(None, "xxxx", None, "api", "key", "source", None, None, "xxxx", "xxxx"),
        resource, method, path
      ),
      None
    )
  }

  private def fabricateGetRoute(marker: String) = new HTTPGetRoute {
    override def apply(requestContext: LambdaRequestContext): LambdaHTTPResponse =
      LambdaHTTPResponse(Option(marker), 200)
  }
}
