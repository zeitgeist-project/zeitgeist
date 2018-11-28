package cloud.zeitgeist.lambda.api.http.routing

import cloud.zeitgeist.lambda.api.http.LambdaHTTPResponse
import cloud.zeitgeist.lambda.api.http.http.LambdaRequestContext

trait HTTPRoute[T] {
  def apply(requestContext: LambdaRequestContext): LambdaHTTPResponse
}
