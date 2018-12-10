package com.virtuslab.zeitgeist.lambda.api.direct

import java.io.{ByteArrayOutputStream, OutputStream}

import com.virtuslab.zeitgeist.lambda.api.{LambdaContext, LambdaSpec}
import org.apache.commons.io.IOUtils
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import org.json4s.{NoTypeHints, _}
import org.scalatest.{MustMatchers, WordSpec}

class DirectLambdaSpec extends WordSpec with MustMatchers with LambdaSpec {
  protected implicit val formats = Serialization.formats(NoTypeHints)

  "Running a direct request to lambda" should {
    "handle simple case with String input / simple json response" in {
      val inputJson = write("testInput")
      val outputStream = new ByteArrayOutputStream()

      val server = new LambdaServer("bam")
      server.handleRequest(IOUtils.toInputStream(inputJson), outputStream, new ContextImpl)

      val output = new String(outputStream.toByteArray)

      output must be("\"bam\"")
    }

    "handle simple case with String input / case class output" in {
      val inputJson = write("testInput")

      val outputStream1 = new ByteArrayOutputStream()
      val result1 = TestResult("string", 4, None)
      val server1 = new LambdaServer(result1)
      server1.handleRequest(IOUtils.toInputStream(inputJson), outputStream1, new ContextImpl)

      val output1 = new String(outputStream1.toByteArray)
      output1 must be(write(result1))


      val outputStream2 = new ByteArrayOutputStream()
      val result2 = TestResult("another", 4, Some(15L))
      val server2 = new LambdaServer(result2)
      server2.handleRequest(IOUtils.toInputStream(inputJson), outputStream2, new ContextImpl)

      val output2 = new String(outputStream2.toByteArray)
      output2 must be(write(result2))
    }

    "handle no input" in {
      val outputStream = new ByteArrayOutputStream()

      val server = new LambdaServer("done")
      server.handleRequest(IOUtils.toInputStream(""), outputStream, new ContextImpl)

      val output = new String(outputStream.toByteArray)

      output must be("\"done\"")
    }

  }
}

class LambdaServer[T <: AnyRef](returnVal: T) extends DirectLambdaStreamingHandler {
  override protected def handleEvent(json: JValue, output: OutputStream)(implicit ctx: LambdaContext) {
    writeJson(output, returnVal)
  }
}

case class TestResult(val1: String, val2: Int, val3: Option[Long])