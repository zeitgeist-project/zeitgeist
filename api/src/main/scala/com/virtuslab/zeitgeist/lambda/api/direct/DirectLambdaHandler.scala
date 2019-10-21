package com.virtuslab.zeitgeist.lambda.api.direct

import java.io.{InputStream, OutputStream}
import java.nio.charset.StandardCharsets.UTF_8

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.virtuslab.zeitgeist.lambda.api.JsonSupport.{readJsonValue, toJsonString}
import com.virtuslab.zeitgeist.lambda.api.LambdaContext
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.scala.Logging

abstract class DirectLambdaHandler[I: Manifest, O] extends RequestStreamHandler with Logging {
  override def handleRequest(inputStream: InputStream, outputStream: OutputStream, context: Context): Unit = {
    val event = IOUtils.toString(inputStream, UTF_8)
    val ctx = new LambdaContext(context)
    processWithLogging(event, ctx) {
      val input = readJsonValue[I](event)
      val output = toJsonString[O](processMessage(input, ctx))
      outputStream.write(output.getBytes(UTF_8))
      outputStream.close()
    }
  }

  def processWithLogging[T](input: String, ctx: LambdaContext)(f: => T): T = {
    logger.info(s"[${ctx.awsRequestId}] Started\n$input")
    try {
      val result = f
      logger.info(s"[${ctx.awsRequestId}] Completed")
      result
    } catch {
      case e: Throwable =>
        logger.error(s"[${ctx.awsRequestId}] Failed", e)
        throw e
    }
  }

  def processMessage(input: I, ctx: LambdaContext): O
}
