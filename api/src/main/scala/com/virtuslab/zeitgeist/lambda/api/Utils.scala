package com.virtuslab.zeitgeist.lambda.api

import java.io.OutputStream
import scala.util.Try

import com.virtuslab.zeitgeist.lambda.api.JsonSupport.toJsonString
import org.apache.commons.io.IOUtils

object Utils {
  def serializeResponse[T <: AnyRef](output: OutputStream, response: T)(implicit ctx: LambdaContext): Unit =
    Try {
      val responseString = toJsonString(response)
      IOUtils.write(responseString, output)
      ctx.log.debug(s"Response: $responseString")
    }.recover {
      case e: Exception =>
        ctx.log.error("Error while writing response\n" + e.getMessage)
        throw new IllegalStateException(e)
    }.get
}
