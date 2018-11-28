/*
 * Copyright (c) 2016 Brendan McAdams & Thomas Lockney
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cloud.zeitgeist.lambda.api.http.macros

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.language.postfixOps
import scala.reflect.macros.blackbox

object LambdaHTTPApi {
  // todo - check for companion object and reject
  def annotation_impl(c: blackbox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    import Flag._

    val p = c.enclosingPosition

    val inputs = annottees.map(_.tree).toList



    val result: Tree = inputs match {
      case (cls @ q"$mods class $name[..$tparams] extends ..$parents { ..$body }") :: Nil if mods.hasFlag(ABSTRACT) ⇒
        c.abort(p, "! The @LambdaHTTPApi annotation is not valid on abstract classes.")
        cls
      // todo - detect and handle companion object!
      case (cls @ q"$mods class $name[..$tparams] extends ..$parents { ..$body }") :: Nil ⇒
        //val baseName = name.decodedName.toString
        //val handlerName = TermName(s"$baseName$$RequestHandler")
        //val handlerName = name.toTermName

        val handlerName = name.asInstanceOf[TypeName].toTermName

        val obj = q"""
        $mods object $handlerName
          extends ..$parents
          with cloud.zeitgeist.lambda.api.http.HTTPHandler
          with cloud.zeitgeist.lambda.api.CoreLambdaApi
          with cloud.zeitgeist.lambda.api.http.HTTPResponses {
            import cloud.zeitgeist.lambda.api.http.LambdaHTTPResponse
            import cloud.zeitgeist.lambda.api.http.http._

            ..$body
          }
        """


        val cls = q"""
        @cloud.zeitgeist.lambda.api.http.macros.LambdaHTTPApiInternal
        class $name[..$tparams] extends cloud.zeitgeist.lambda.api.http.HTTPApp {
          def newHandler: cloud.zeitgeist.lambda.api.http.HTTPHandler =
            ${name.asInstanceOf[TypeName].toTermName}


        }
        """

        println(s"$cls; $obj")
        q"$cls; $obj"

      case Nil ⇒
        c.abort(p, s"Cannot annotate an empty Tree.")
      case _ ⇒
        c.abort(p, s"! The @LambdaHTTPApi Annotation is only valid on non-abstract Classes")
    }

    //c.info(p, "result: " + result, force = true)

    c.Expr[Any](result)

  }

}

@compileTimeOnly("Setup the macro paradise compiler plugin to enable expansion of macro annotations.")
class LambdaHTTPApi extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro LambdaHTTPApi.annotation_impl

}

/**
  * Internal annotation used for automatic finding out of handler classes.
  */
class LambdaHTTPApiInternal extends StaticAnnotation