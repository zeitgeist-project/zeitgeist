package com.virtuslab.zeitgeist.lambda.api.http

import scala.annotation.implicitNotFound
import com.virtuslab.zeitgeist.lambda.api.JsonSupport._
import com.fasterxml.jackson.databind.JsonNode

object HTTPResponseMarshallers extends HTTPResponseMarshallers

trait HTTPResponseMarshallers {

  @implicitNotFound("Unable to determine how to marshal ${T} to a JSON4S JValue; please provide an implicit instance of HTTPResponseMarshaller[${T}]")
  trait HTTPResponseMarshaller[-T] {
    def apply(value: T): Option[String]
  }

  implicit object JValueResponseMarshaller extends HTTPResponseMarshaller[JsonNode] {
    def apply(value: JsonNode) = Option(toJsonString(value))
  }

  implicit object ClassResponseMarshaller extends HTTPResponseMarshaller[AnyRef] {
    def apply(value: AnyRef) = Option(toJsonString(value))
  }

}
