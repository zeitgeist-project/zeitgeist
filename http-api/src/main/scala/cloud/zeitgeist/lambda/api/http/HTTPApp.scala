package cloud.zeitgeist.lambda.api.http

import java.io.{InputStream, OutputStream}

import cloud.zeitgeist.lambda.api.{CoreLambdaApi, LambdaContext}
import cloud.zeitgeist.lambda.api.http.http.LambdaHTTPRequest
import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import org.apache.commons.io.IOUtils
import org.json4s.NoTypeHints
import org.json4s.jackson.JsonMethods.{parse, pretty, render}
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.io.Source
import scala.util.Try

trait HTTPApp extends RequestStreamHandler {

  def newHandler: HTTPHandler

  protected implicit val formats = Serialization.formats(NoTypeHints)

  final override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {

    val ctx = new LambdaContext(context)
    val log = ctx.log

    val inputString = Source.fromInputStream(input).mkString
    log.debug(s"Passed input is:\n${inputString}")
    val json = parse(inputString)

    val req = json.extract[LambdaHTTPRequest]

    log.debug(s"Input: ${pretty(render(json))}")

    val response = newHandler.routeRequest(req, ctx)

    Try {
      val responseString = write(response)
      IOUtils.write(responseString, output)
      log.debug(s"Response: ${responseString}")
    }.recover {
      case e: Exception =>
        log.error("Error while writing response\n" + e.getMessage)
        throw new IllegalStateException(e)
    }.get
  }
}
