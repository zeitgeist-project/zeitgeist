package cloud.zeitgeist.lambda.api.http.routing

case class MethodRoute(method: String, route: HTTPRoute[_])
