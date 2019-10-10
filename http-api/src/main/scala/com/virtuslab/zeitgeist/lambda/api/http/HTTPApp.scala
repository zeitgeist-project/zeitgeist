package com.virtuslab.zeitgeist.lambda.api.http

import java.io.{InputStream, OutputStream}
import scala.io.Source

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.virtuslab.zeitgeist.lambda.api.JsonSupport._
import com.virtuslab.zeitgeist.lambda.api.http.http.LambdaHTTPRequest
import com.virtuslab.zeitgeist.lambda.api.{LambdaContext, Utils}

trait HTTPApp extends RequestStreamHandler {

  def newHandler: HTTPHandler

  final override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {

    implicit val ctx: LambdaContext = new LambdaContext(context)
    import ctx.log

    val inputString = Source.fromInputStream(input).mkString
    log.debug(s"Passed input is:\n$inputString")

    val req = readJsonValue[LambdaHTTPRequest](input)

    log.debug(s"Input: ${toJsonString(req)}")

    val response = newHandler.routeRequest(req, ctx)

    Utils.serializeResponse(output, response)
  }
}
