package com.virtuslab.zeitgeist.lambda.api.http

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.virtuslab.zeitgeist.lambda.api.http.http.{HTTPMethod, LambdaHTTPRequest, LambdaRequestContext}
import com.virtuslab.zeitgeist.lambda.api.http.routing.{HTTPRoute, MethodRoute, PathRoutingResolver, RouteRequest}
import com.virtuslab.zeitgeist.lambda.api.LambdaContext
import com.virtuslab.zeitgeist.lambda.api.direct.DirectLambdaHandler
import org.apache.logging.log4j.scala.Logger

trait HTTPHandler extends RequestStreamHandler {

  protected val routeBuilder = Map.newBuilder[(HTTPMethod, String), HTTPRoute[_]]

  protected val pathResolver = new PathRoutingResolver

  lazy val routes = routeBuilder.result

  private val directLambdaDelegate = new DirectLambdaHandler[LambdaHTTPRequest, LambdaHTTPResponse] {
    override def processMessage(input: LambdaHTTPRequest, ctx: LambdaContext): LambdaHTTPResponse =
      routeRequest(input, ctx)
  }

  override def handleRequest(inputStream: InputStream, outputStream: OutputStream, context: Context): Unit =
    directLambdaDelegate.handleRequest(inputStream, outputStream, context)

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
