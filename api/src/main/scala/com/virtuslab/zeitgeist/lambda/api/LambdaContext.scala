package com.virtuslab.zeitgeist.lambda.api

import com.amazonaws.services.lambda.runtime.{ClientContext, CognitoIdentity, Context}
import org.apache.logging.log4j.scala.Logger
import org.apache.logging.log4j.scala.Logging

class LambdaContext(ctx: Context) extends Logging {
  val log: Logger = logger

  def identity: Option[CognitoIdentity] = Option(ctx.getIdentity)

  def clientContext: Option[ClientContext] = Option(ctx.getClientContext)

  def memoryLimitInMB: Int = ctx.getMemoryLimitInMB

  def remainingTimeInMillis: Int = ctx.getRemainingTimeInMillis

  def awsRequestId: String = ctx.getAwsRequestId

  def functionName: String = ctx.getFunctionName

  def logGroupName: Option[String] = Option(ctx.getLogGroupName)

  def logStreamName: Option[String] = Option(ctx.getLogStreamName)
}