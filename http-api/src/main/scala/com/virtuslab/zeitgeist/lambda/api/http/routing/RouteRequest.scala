package com.virtuslab.zeitgeist.lambda.api.http.routing

import com.virtuslab.zeitgeist.lambda.api.http.http.LambdaHTTPRequest

case class RouteRequest[T](route: HTTPRoute[T], request: LambdaHTTPRequest)
