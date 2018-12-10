package com.virtuslab.zeitgeist.lambda.api

import com.amazonaws.services.lambda.runtime.{ClientContext, CognitoIdentity, Context, LambdaLogger}
import org.apache.logging.log4j.scala.Logging

import scala.util.Random

trait LambdaSpec {
  def generateContext: LambdaContext = {
    new LambdaContext(new ContextImpl)
  }

  class ContextImpl extends Context with Logging {
    override val getFunctionName: String = Random.nextString(10)

    override def getRemainingTimeInMillis: Int = 10

    override val getLogger: LambdaLogger = new LambdaLogger {
      override def log(message: String): Unit = logger.info(message)

      override def log(message: Array[Byte]): Unit = logger.info(new String(message))
    }

    override def getFunctionVersion: String = "1.0"

    override def getMemoryLimitInMB: Int = 192

    override def getClientContext: ClientContext = ???

    override def getLogStreamName: String = ???

    override val getInvokedFunctionArn: String = Random.nextString(10)

    override def getIdentity: CognitoIdentity = ???

    override def getLogGroupName: String = ???

    override val getAwsRequestId: String = Random.nextString(10)
  }
}
