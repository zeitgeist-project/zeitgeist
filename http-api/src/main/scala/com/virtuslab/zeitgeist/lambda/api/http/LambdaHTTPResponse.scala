package com.virtuslab.zeitgeist.lambda.api.http

final case class LambdaHTTPResponse(
                                     body: Option[String] = None,
                                     statusCode: Int = 200,
                                     headers: Map[String, Any] = Map.empty[String, Any]
                                   )
