package com.virtuslab.zeitgeist.lambda.api.http

import com.virtuslab.zeitgeist.lambda.api.{CoreLambdaApi, LambdaContext}

package object http extends CoreLambdaApi with HTTPResponses {

  object HTTPMethod {
    def apply(method: String): HTTPMethod = method match {
      case "GET" ⇒ GET
      case "POST" ⇒ POST
      case "PUT" ⇒ PUT
      case "DELETE" ⇒ DELETE
      case "HEAD" ⇒ HEAD
      case "OPTIONS" ⇒ OPTIONS
      case "PATCH" ⇒ PATCH
      case _ ⇒
        throw new IllegalArgumentException(s"Unsupported/unknown HTTP Method '$method'")
    }
  }

  sealed abstract class HTTPMethod
  case object GET extends HTTPMethod
  case object POST extends HTTPMethod
  case object PUT extends HTTPMethod
  case object DELETE extends HTTPMethod
  case object HEAD extends HTTPMethod
  case object OPTIONS extends HTTPMethod
  case object PATCH extends HTTPMethod

  case class LambdaRequestContext(request: LambdaHTTPRequest, context: LambdaContext)

  case class LambdaHTTPRequest(
    resource: String,
    path: String, // todo - wire into an object that can extract vars
    httpMethod: String, // todo - in place validation
    requestContext: LambdaHTTPRequestContext,
    body: Option[String],
    headers: Map[String, String] = Map.empty,
    queryStringParameters: Map[String, String] = Map.empty,
    pathParameters: Map[String, String] = Map.empty,
    stageVariables: Map[String, String] = Map.empty
  )

  case class CognitoData(
    cognitoIdentityPoolId: Option[String],
    accountId: String,
    cognitoIdentityId: Option[String],
    caller: String,
    apiKey: String,
    sourceIp: String,
    cognitoAuthenticationType: Option[String],
    cognitoAuthenticationProvider: Option[String],
    userArn: String,
    user: String
  )

  case class LambdaHTTPRequestContext(
    accountId: String,
    resourceId: String,
    stage: String,
    requestId: String,
    identity: CognitoData,
    resourcePath: String,
    httpMethod: String,
    apiId: String
  )
}
