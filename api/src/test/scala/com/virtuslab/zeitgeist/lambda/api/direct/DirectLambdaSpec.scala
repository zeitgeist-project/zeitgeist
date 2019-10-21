package com.virtuslab.zeitgeist.lambda.api.direct

import java.io.ByteArrayOutputStream

import com.fasterxml.jackson.databind.JsonMappingException
import com.virtuslab.zeitgeist.lambda.api.JsonSupport._
import com.virtuslab.zeitgeist.lambda.api.{LambdaContext, LambdaSpec}
import org.apache.commons.io.IOUtils
import org.scalatest.{MustMatchers, WordSpec}

class DirectLambdaSpec extends WordSpec with MustMatchers with LambdaSpec {

  class TestLambda[T](returnVal: T) extends DirectLambdaHandler[String, T] {
    override def processMessage(input: String, ctx: LambdaContext): T = returnVal
  }

  case class TestResult(val1: String, val2: Int, val3: Option[Long])

  "Running a direct request to lambda" should {
    "handle simple case with String input / simple json response" in {
      val inputJson = toJsonString("testInput")
      val outputStream = new ByteArrayOutputStream()

      val server = new TestLambda("bam")
      server.handleRequest(IOUtils.toInputStream(inputJson), outputStream, new ContextImpl)

      val output = new String(outputStream.toByteArray)

      output must be("\"bam\"")
    }

    "handle simple case with String input / case class output" in {
      val inputJson = toJsonString("testInput")

      val outputStream1 = new ByteArrayOutputStream()
      val result1 = TestResult("string", 4, None)
      val server1 = new TestLambda(result1)
      server1.handleRequest(IOUtils.toInputStream(inputJson), outputStream1, new ContextImpl)

      val output1 = new String(outputStream1.toByteArray)
      output1 must be(toJsonString(result1))


      val outputStream2 = new ByteArrayOutputStream()
      val result2 = TestResult("another", 4, Some(15L))
      val server2 = new TestLambda(result2)
      server2.handleRequest(IOUtils.toInputStream(inputJson), outputStream2, new ContextImpl)

      val output2 = new String(outputStream2.toByteArray)
      output2 must be(toJsonString(result2))
    }

    "throw exception if input is not a valid json" in {
      val outputStream = new ByteArrayOutputStream()

      val server = new TestLambda("done")

      an[JsonMappingException] shouldBe thrownBy {
        server.handleRequest(IOUtils.toInputStream(""), outputStream, new ContextImpl)
      }
    }

    "handle Unit types as output" in {
      val inputJson = toJsonString("string")
      val outputStream = new ByteArrayOutputStream()

      val server = new TestLambda[Unit](Unit)
      server.handleRequest(IOUtils.toInputStream(inputJson), outputStream, new ContextImpl)

      val output = new String(outputStream.toByteArray)

      output must be("")
    }
  }
}
