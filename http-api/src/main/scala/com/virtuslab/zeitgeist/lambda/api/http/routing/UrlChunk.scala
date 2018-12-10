package com.virtuslab.zeitgeist.lambda.api.http.routing

sealed trait UrlChunk {
  def isStatic: Boolean
}

case class StaticUrlChunk(urlPart: String) extends UrlChunk {
  override val isStatic: Boolean = true
}

case class DynamicUrlChunk(paramName: String) extends UrlChunk {
  override val isStatic: Boolean = false

  override def hashCode(): Int = 1

  override def equals(obj: scala.Any): Boolean = obj match {
    case DynamicUrlChunk(_) => true
    case _ => false
  }
}

