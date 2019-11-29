package com.virtuslab.zeitgeist.lambda.api

import java.io.InputStream
import scala.runtime.BoxedUnit

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

object JsonSupport {
  lazy val objectMapper: ObjectMapper with ScalaObjectMapper = {
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.registerModule(baseFormats)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper
  }

  val baseFormats: Module = {
    val module = new SimpleModule()
    module.addSerializer(new JsonSerializer[BoxedUnit] {
      override def serialize(value: BoxedUnit, gen: JsonGenerator, serializers: SerializerProvider): Unit =
        JsonNodeFactory.instance.nullNode()

      override def handledType(): Class[BoxedUnit] = classOf[BoxedUnit]
    })
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
