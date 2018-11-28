package cloud.zeitgeist.lambda.api.http.routing

import cloud.zeitgeist.lambda.api.Logger
import cloud.zeitgeist.lambda.api.http.http.LambdaHTTPRequest

import scala.annotation.tailrec
import scala.collection.mutable

class PathRoutingResolver {
  private val routeMappings = mutable.HashMap[List[UrlChunk], Mappings]()

  private val DynamicParamPattern = "\\{(.+)\\}".r

  def bindPath(path: String, routing: MethodRoute): PathRoutingResolver = {
    val chunkedKey = mapToChunks(path)
    val mapping = routeMappings.getOrElseUpdate(chunkedKey, Mappings())

    mapping.routes.get(routing.method) match {
      case None =>
        mapping.routes.put(routing.method, routing.route)
        this

      case Some(_) =>
        throw new IllegalArgumentException(s"Attempted to bind same route twice: ${path}")
    }
  }

  def resolveRequestRoute(request: LambdaHTTPRequest)(implicit log: Logger): Option[RouteRequest[_]] = {
    val startTime = System.currentTimeMillis()

    val pathParts = request.path.split("/").toList.tail

    val potentialMatches = routeMappings.filter {
      isRouteMatch(_, pathParts, request.httpMethod)
    }

    val maybePickedBestChunks = pickBestMatch(potentialMatches.keySet, pathParts)

    val maybeRequestRoute = for {
      pickedBestChunks <- maybePickedBestChunks
      mapping <- routeMappings.get(pickedBestChunks)
      route <- mapping.routes.get(request.httpMethod)
    } yield {
      RouteRequest(
        route,
        request.copy(pathParameters = request.pathParameters ++ constructPathParams(pickedBestChunks, pathParts)),
      )
    }


    log.debug(s"Route for path: `${request.path}` resolved in: ${System.currentTimeMillis() - startTime} ms")
    maybeRequestRoute
  }

  private def isRouteMatch(chunkToRoute: (List[UrlChunk], Mappings), pathParts: List[String], method: String) = {
    matchChunked(chunkToRoute._1, pathParts) && chunkToRoute._2.routes.contains(method)
  }

  @tailrec
  private def matchChunked(chunks: List[UrlChunk], pathParts: List[String]): Boolean = {
    if(chunks.size == pathParts.size) {
      (chunks, pathParts) match {
        case (Nil, Nil) =>
          true

        case (chunkHead :: chunkTail, pathHead :: pathTail) =>
          chunkHead match {
            case DynamicUrlChunk(_) =>
              matchChunked(chunkTail, pathTail)

            case StaticUrlChunk(pathFragment) if pathFragment == pathHead =>
              matchChunked(chunkTail, pathTail)

            case _ =>
              false
          }

        case (_, _) =>
          assert(false,
            s"This shouldn't happen - apparently we have proposed route (${chunks.mkString("/")}) and " +
            s"actual path (${pathParts.mkString("/")}) of different length")
          false
      }
    } else {
      false
    }
  }

  @tailrec
  private def pickBestMatch(potentialMatches: Iterable[List[UrlChunk]], pathParts: List[String]):
  Option[List[UrlChunk]] = {
    (potentialMatches, pathParts) match {
      case (_, Nil) =>
        if(potentialMatches.size > 1) {
          throw new IllegalStateException(s"""
                                    |We have more than one  match for path: ${pathParts.mkString("/")}.
                                    |These matches are: ${potentialMatches.mkString("\n")}
                                    |""".stripMargin)
        }

        potentialMatches.headOption

      case (matches, _ :: pathTail) =>
        pickBestMatch(attemptToPickStaticChunks(matches, pathParts), pathTail)
    }
  }

  private def constructPathParams(chunkedPath: List[UrlChunk], pathParts: List[String]): Map[String, String] = {
    require(chunkedPath.size == pathParts.size)

    @tailrec
    def construct(chunkedPath: List[UrlChunk], pathParts: List[String], params: Map[String, String]):
    Map[String, String] = (chunkedPath, pathParts) match {
      case (Nil, Nil) =>
        params

      case (StaticUrlChunk(_) :: chunkedTail, _ :: pathTail) =>
        construct(chunkedTail, pathTail, params)

      case (DynamicUrlChunk(paramName) :: chunkedTail, pathHead :: pathTail) =>
        construct(chunkedTail, pathTail, params + (paramName -> pathHead))

      case (_, _) =>
        assert(false,
          s"This shouldn't happen - apparently we have chosen route (${chunkedPath.mkString("/")} of different length " +
          s"than actual path (${pathParts.mkString("/")})"
        )
        Map.empty
    }

    construct(chunkedPath, pathParts, Map.empty)
  }

  private def attemptToPickStaticChunks(potentialMatches: Iterable[List[UrlChunk]], remainingPathParts: List[String]):
  Iterable[List[UrlChunk]] = {
    val staticPaths = potentialMatches.filter { potentialMatch =>
      val size = potentialMatches.size
      potentialMatch
        .slice(size - remainingPathParts.size, size)
        .headOption
        .map(_.isStatic)
        .getOrElse(false)
    }

    if (staticPaths.isEmpty) {
      potentialMatches
    } else {
      staticPaths
    }
  }

  private def mapToChunks(path: String): List[UrlChunk] = {
    require(path.startsWith("/"))
    val pathChunks = path.split("/").toList.tail  // making sure we grab parts after first /

    pathChunks.map {
      case DynamicParamPattern(matchedParam) => DynamicUrlChunk(matchedParam)
      case pathPart => StaticUrlChunk(pathPart)
    }
  }
}
