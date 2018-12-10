package com.virtuslab.zeitgeist.lambda.api.http

import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import org.json4s.{NoTypeHints, _}

import scala.annotation.implicitNotFound

object HTTPResponseMarshallers extends HTTPResponseMarshallers

trait HTTPResponseMarshallers {
  implicit val formats = Serialization.formats(NoTypeHints)

  @implicitNotFound("Unable to determine how to marshal ${T} to a JSON4S JValue; please provide an implicit instance of HTTPResponseMarshaller[${T}]")
  trait HTTPResponseMarshaller[-T] {
    def apply(value: T): Option[String]
  }

  implicit object JValueResponseMarshaller extends HTTPResponseMarshaller[JValue] {
    def apply(value: JValue) = Option(compact(value))
  }

  implicit object ClassResponseMarshaller extends HTTPResponseMarshaller[AnyRef] {
    def apply(value: AnyRef) = Option(write(value))
  }

}

// vim: set ts=2 sw=2 sts=2 et:
