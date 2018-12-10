package com.virtuslab.zeitgeist.lambda.api.http

import java.io.OutputStream

import com.virtuslab.zeitgeist.lambda.api.direct.DirectLambdaStreamingHandler
import com.virtuslab.zeitgeist.lambda.api.http.routing.{HTTPRoute, MethodRoute, PathRoutingResolver, RouteRequest}
import com.virtuslab.zeitgeist.lambda.api.http.http.{HTTPMethod, LambdaHTTPRequest, LambdaRequestContext}
import com.virtuslab.zeitgeist.lambda.api.LambdaContext
import org.apache.logging.log4j.scala.Logger
import org.json4s.JValue

trait HTTPHandler extends DirectLambdaStreamingHandler {

  protected val routeBuilder = Map.newBuilder[(HTTPMethod, String), HTTPRoute[_]]

  protected val pathResolver = new PathRoutingResolver

  lazy val routes = routeBuilder.result


  override def handleEvent(json: JValue, output: OutputStream)(implicit ctx: LambdaContext) {
    val req = json.extract[LambdaHTTPRequest]
    routeRequest(req, ctx)
  }

  def routeRequest(request: LambdaHTTPRequest, context: LambdaContext): LambdaHTTPResponse = {
    handlerByResource(request)
      .orElse(handlerByPath(request)(context.log)) match {
      case Some(RouteRequest(handler, request)) => handler(LambdaRequestContext(request, context))
      case None ⇒ LambdaHTTPResponse(statusCode = 404)
    }
  }

  def addRoute(method: HTTPMethod, route: String, handler: HTTPRoute[_]): Unit = {
    routeBuilder += (method -> route) -> handler
    pathResolver.bindPath(route, MethodRoute(method.toString, handler))
  }

  private def handlerByResource(request: LambdaHTTPRequest): Option[RouteRequest[_]] = {
    routes
      .get(HTTPMethod(request.httpMethod) -> request.resource)
      .map { route =>
        RouteRequest(route, request)
      }
  }

  private def handlerByPath(request: LambdaHTTPRequest)(implicit log: Logger): Option[RouteRequest[_]] = {
    pathResolver.resolveRequestRoute(request)
  }
}
