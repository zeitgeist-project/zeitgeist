package com.virtuslab.zeitgeist.lambda.api.direct

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler, RequestStreamHandler}
import com.virtuslab.zeitgeist.lambda.api.LambdaContext
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.scala.Logging
import org.json4s.jackson.JsonMethods.{parse, pretty, render}
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.json4s.{JValue, NoTypeHints, _}

import scala.annotation.StaticAnnotation
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.io.Source
import scala.util.Try

class DirectLambda extends StaticAnnotation

trait DirectLambdaStreamingHandler extends RequestStreamHandler with Logging {
  protected implicit val formats = Serialization.formats(NoTypeHints)

  protected def handleEvent(json: JValue, output: OutputStream)(implicit ctx: LambdaContext): Unit

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {

    val ctx = new LambdaContext(context)

    val inputString = Source.fromInputStream(input).mkString
    logger.debug(s"Passed input is:\n${inputString}")

    val json = if(inputString == null || inputString.trim.isEmpty) {
      JNothing
    } else {
      parse(inputString)
    }
    logger.debug(s"Input: ${pretty(render(json))}")

    handleEvent(json, output)(ctx)
  }

  protected def writeJson[T <: AnyRef](output: OutputStream, response: T)(implicit ctx: LambdaContext) {
    Try {
      val responseString = write(response)
      IOUtils.write(responseString, output)
      logger.debug(s"Response: ${responseString}")
    }.recover {
      case e: Exception =>
        logger.error("Error while writing response\n" + e.getMessage)
      throw new IllegalStateException(e)
    }.get
  }
}

trait DirectLambdaEventHandler[Response] extends RequestHandler[java.util.Map[String, Any], Response] with Logging {
  protected def handleEvent(input: Any)(implicit ctx: LambdaContext): Response

  override def handleRequest(inputMap: java.util.Map[String, Any], context: Context): Response = {
    val ctx = new LambdaContext(context)

    val input = inputMap.asScala

    logger.debug(s"Passed input is:\n${input}")
    val response = handleEvent(input)(ctx)
    logger.debug(s"Genereted output is:\n${input}")

    response
  }
}
