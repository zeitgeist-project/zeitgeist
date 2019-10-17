package com.virtuslab.zeitgeist.lambda.api

import java.io.InputStream

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

object JsonSupport {
  lazy val objectMapper: ObjectMapper with ScalaObjectMapper = {
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper
  }

  def toJsonString[T](input: T, prettyPrint: Boolean = true): String = {
    val writer =
      if (prettyPrint)
        objectMapper.writerWithDefaultPrettyPrinter()
      else
        objectMapper.writer()

    writer.writeValueAsString(input)
  }

  def readJsonValue[T: Manifest](is: InputStream): T =
    objectMapper.readValue[T](is)

  def readJsonValue[T: Manifest](s: String): T =
    objectMapper.readValue[T](s)
}
