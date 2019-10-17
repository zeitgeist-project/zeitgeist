package com.virtuslab.zeitgeist.lambda.api.direct

import java.io.{InputStream, OutputStream}
import scala.annotation.StaticAnnotation
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.io.Source

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler, RequestStreamHandler}
import com.fasterxml.jackson.databind.JsonNode
import com.virtuslab.zeitgeist.lambda.api.JsonSupport._
import com.virtuslab.zeitgeist.lambda.api.LambdaContext
import org.apache.logging.log4j.scala.Logging

class DirectLambda extends StaticAnnotation

trait DirectLambdaStreamingHandler extends RequestStreamHandler with Logging {

  protected def handleEvent(json: JsonNode, output: OutputStream)(implicit ctx: LambdaContext): Unit

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    implicit val ctx: LambdaContext = new LambdaContext(context)

    val inputString = Source.fromInputStream(input).mkString
    logger.debug(s"Passed input is:\n$inputString")

    val json = if (inputString == null || inputString.trim.isEmpty) {
      objectMapper.getNodeFactory.nullNode()
    } else {
      objectMapper.readTree(inputString)
    }

    logger.debug(s"Input: ${objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)}")

    handleEvent(json, output)
  }
}

trait DirectLambdaEventHandler[Response] extends RequestHandler[java.util.Map[String, Any], Response] with Logging {
  protected def handleEvent(input: Any)(implicit ctx: LambdaContext): Response

  override def handleRequest(inputMap: java.util.Map[String, Any], context: Context): Response = {
    val ctx = new LambdaContext(context)

    val input = inputMap.asScala

    logger.debug(s"Passed input is:\n$input")
    val output = handleEvent(input)(ctx)
    logger.debug(s"Generated output is:\n$output")

    output
  }
}
