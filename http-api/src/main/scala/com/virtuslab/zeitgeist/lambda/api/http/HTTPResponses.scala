package com.virtuslab.zeitgeist.lambda.api.http

import com.virtuslab.zeitgeist.lambda.api.ResponseMagnet
import com.virtuslab.zeitgeist.lambda.api.http.HTTPResponseMarshallers.HTTPResponseMarshaller

trait HTTPResponses {

  trait HTTPResponseMagnet extends ResponseMagnet {
    type Result = LambdaHTTPResponse
  }

  implicit def fromStatusObject[T : HTTPResponseMarshaller](tuple: (StatusCode, T)): HTTPResponseMagnet =
    () => {
        val marshaller = implicitly[HTTPResponseMarshaller[T]]
        LambdaHTTPResponse(
          statusCode = tuple._1.httpStatus,
          body = marshaller(tuple._2)
        )
    }

  implicit def fromString(body: String): HTTPResponseMagnet =
    () => LambdaHTTPResponse(
      body = Option(body)
    )

  implicit def fromStatusCode(status: StatusCode): HTTPResponseMagnet =
    () => LambdaHTTPResponse(
      statusCode = status.httpStatus
    )

  implicit def fromInt(status: Int): HTTPResponseMagnet =
    () => LambdaHTTPResponse(
      statusCode = status
    )

  implicit def fromObject[T : HTTPResponseMarshaller](body: T): HTTPResponseMagnet =
    () => {
      val marshaller = implicitly[HTTPResponseMarshaller[T]]
      LambdaHTTPResponse(
        body = marshaller(body)
      )
    }


  sealed abstract class StatusCode(val httpStatus: Int)

  object HTTPStatus
    extends HTTPSuccesses
    with HTTPRedirections
    with HTTPClientErrors
    with HTTPServerErrors {

    def apply(statusCode: Int): StatusCode = new StatusCode(statusCode) {}
  }

  trait HTTPSuccesses {
    case object OK extends StatusCode(200)
    case object Created extends StatusCode(201)
    case object Accepted extends StatusCode(202)
    case object NoContent extends StatusCode(204)
  }

  trait HTTPRedirections {
    case object MovedPermanently extends StatusCode(301)
    case object NotModified extends StatusCode(304)
    case object TemporaryRedirect extends StatusCode(307)
    case object PermanentRedirect extends StatusCode(308)
  }

  trait HTTPClientErrors {
    case object BadRequest extends StatusCode(400)
    case object Unauthorized extends StatusCode(401)
    case object Forbidden extends StatusCode(403)
    case object NotFound extends StatusCode(404)
    case object NotAcceptable extends StatusCode(406)
    case object RequestTimeout extends StatusCode(408)
    // "Unavailable for Legal Reasons"
    case object DoublePlusUngood extends StatusCode(451)

    case object ImATeapot extends StatusCode(418)
  }

  trait HTTPServerErrors {
    case object InternalServerError extends StatusCode(500)
  }

}
