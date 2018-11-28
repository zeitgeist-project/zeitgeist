package cloud.zeitgeist.lambda.api.http

import java.io.OutputStream

import cloud.zeitgeist.lambda.api.direct.DirectLambdaHandler
import cloud.zeitgeist.lambda.api.http.http.{HTTPMethod, LambdaHTTPRequest, LambdaRequestContext}
import cloud.zeitgeist.lambda.api.http.routing.{HTTPRoute, MethodRoute, PathRoutingResolver, RouteRequest}
import cloud.zeitgeist.lambda.api.{LambdaContext, Logger}
import org.json4s.JValue

trait HTTPHandler extends DirectLambdaHandler {

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
