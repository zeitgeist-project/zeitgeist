package cloud.zeitgeist.lambda.api

import com.amazonaws.services.lambda.runtime.LambdaLogger

class Logger(logger: LambdaLogger) {
  object Level extends Enumeration {
    type Level = Value
    val DEBUG, INFO, WARN, ERROR = Value
  }

  def debug(msg: String): Unit = log(Level.DEBUG, msg)

  def info(msg: String): Unit = log(Level.INFO, msg)

  def warn(msg: String): Unit = log(Level.WARN, msg)

  def error(msg: String): Unit = log(Level.ERROR, msg)

  private def log(level: Level.Level, msg: String): Unit = {
    logger.log(s"${level}: ${msg}")
  }
}
