package cloud.zeitgeist.lambda.api.direct

import java.io.{InputStream, OutputStream}

import cloud.zeitgeist.lambda.api.LambdaContext
import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import org.apache.commons.io.IOUtils
import org.json4s.jackson.JsonMethods.{parse, pretty, render}
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.json4s.{JValue, NoTypeHints, _}

import scala.annotation.StaticAnnotation
import scala.io.Source
import scala.util.Try

class DirectLambda extends StaticAnnotation

trait DirectLambdaHandler extends RequestStreamHandler {
  protected implicit val formats = Serialization.formats(NoTypeHints)

  protected def handleEvent(json: JValue, output: OutputStream)(implicit ctx: LambdaContext): Unit

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {

    val ctx = new LambdaContext(context)
    val log = ctx.log

    val inputString = Source.fromInputStream(input).mkString
    log.debug(s"Passed input is:\n${inputString}")

    val json = if(inputString == null || inputString.trim.isEmpty) {
      JNothing
    } else {
      parse(inputString)
    }
    log.debug(s"Input: ${pretty(render(json))}")

    handleEvent(json, output)(ctx)
  }

  protected def writeJson[T <: AnyRef](output: OutputStream, response: T)(implicit ctx: LambdaContext) {
    Try {
      val responseString = write(response)
      IOUtils.write(responseString, output)
      ctx.log.debug(s"Response: ${responseString}")
    }.recover {
      case e: Exception =>
        ctx.log.error("Error while writing response\n" + e.getMessage)
      throw new IllegalStateException(e)
    }.get
  }
}
