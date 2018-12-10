package com.virtuslab.zeitgeist.lambda.api.http.routing

import com.virtuslab.zeitgeist.lambda.api.http.LambdaHTTPResponse
import com.virtuslab.zeitgeist.lambda.api.http.http.LambdaRequestContext

trait HTTPRoute[T] {
  def apply(requestContext: LambdaRequestContext): LambdaHTTPResponse
}
