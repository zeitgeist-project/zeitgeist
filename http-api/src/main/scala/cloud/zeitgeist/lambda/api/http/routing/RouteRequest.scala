package cloud.zeitgeist.lambda.api.http.routing

import cloud.zeitgeist.lambda.api.http.http.LambdaHTTPRequest

case class RouteRequest[T](route: HTTPRoute[T], request: LambdaHTTPRequest)
