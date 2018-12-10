package com.virtuslab.zeitgeist.lambda.api

package object api extends CoreLambdaApi

trait ResponseMagnet {
  type Result
  def apply(): Result
}

trait CoreLambdaApi {

  def complete(magnet: ResponseMagnet): magnet.Result = magnet()
}
