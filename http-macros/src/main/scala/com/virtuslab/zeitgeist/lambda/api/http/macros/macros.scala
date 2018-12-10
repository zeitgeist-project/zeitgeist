package com.virtuslab.zeitgeist.lambda.api.http.macros

import com.virtuslab.zeitgeist.lambda.api.http.LambdaHTTPResponse
import com.virtuslab.zeitgeist.lambda.api.http.http.LambdaRequestContext

package object macros {

  type RouteBlockWithParams = LambdaRequestContext ⇒ LambdaHTTPResponse

  type RouteXBlockWithBodyParams[T] = (LambdaRequestContext, String*) ⇒ LambdaHTTPResponse

  type RouteBlock[T <: Product] =  T ⇒ LambdaHTTPResponse
  type RouteXBlock[T <: Product] =  (T, String*) ⇒ LambdaHTTPResponse

  implicit def route1ArgToRouteX[T <: Product](
    block: (T, String) ⇒ LambdaHTTPResponse
  ): RouteXBlock[T] = (body, args) ⇒ block(body, args(0))

  implicit def route2ArgToRouteX[T <: Product](
    block: (T, String, String) ⇒ LambdaHTTPResponse
  ): RouteXBlock[T] = (body, args) ⇒ block(body, args(0), args(1))



  def get(route: String)(block: RouteBlockWithParams): Any = macro get_impl

  def get_impl(c: scala.reflect.macros.whitebox.Context)(route: c.Expr[String])(block: c.Expr[RouteBlockWithParams]): c.Expr[Any] = {
    import c.universe._

    val obj = q"""
    val handler = new com.virtuslab.zeitgeist.lambda.api.http.routing.HTTPGetRoute {
      def apply(requestContext: LambdaRequestContext): LambdaHTTPResponse = {
        $block(requestContext)
      }
    }

    addRoute(com.virtuslab.zeitgeist.lambda.api.http.http.GET, $route, handler)
    """

    c.Expr[Any](obj)
  }

  def head(route: String)(block: RouteBlockWithParams): Any = macro head_impl

  def head_impl(c: scala.reflect.macros.whitebox.Context)(route: c.Expr[String])(block: c.Expr[RouteBlockWithParams]): c.Expr[Any] = {
    import c.universe._

    val obj = q"""
    val handler = new com.virtuslab.zeitgeist.lambda.api.http.routing.HTTPHeadRoute {
      def apply(requestContext: LambdaRequestContext): LambdaHTTPResponse = {
        $block(requestContext)
      }
    }

    addRoute(com.virtuslab.zeitgeist.lambda.api.http.http.HEAD, $route, handler)
    """

    c.Expr[Any](obj)
  }


  def options(route: String)(block: RouteBlockWithParams): Any = macro options_impl


  def options_impl(c: scala.reflect.macros.whitebox.Context)(route: c.Expr[String])(block: c.Expr[RouteBlockWithParams]): c.Expr[Any] = {
    import c.universe._

    val obj = q"""
    val handler = new com.virtuslab.zeitgeist.lambda.api.http.routing.HTTPOptionsRoute {
      def apply(requestContext: LambdaRequestContext): LambdaHTTPResponse = {
        $block(requestContext)
      }
    }

    addRoute(com.virtuslab.zeitgeist.lambda.api.http.http.OPTIONS, $route, handler)
    """

    c.Expr[Any](obj)
  }

  def postX[T <: Product](route: String)(block: RouteXBlockWithBodyParams[T]): Any =
    macro postX_impl[T]


  def postX_impl[T <: Product : c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context)(route: c.Expr[String])(block: c.Expr[RouteXBlockWithBodyParams[T]]): c.Expr[Any] = {
    import c.universe._

    val tpe = weakTypeOf[T]

    if (tpe =:= typeOf[Nothing])
      c.abort(c.enclosingPosition, "POST routes require a case class argument describing how the JSON body should be deserialized. Please call it as `post[<TYPE>](<route>) ...`")

    val obj = q"""
    val handler = new com.virtuslab.zeitgeist.lambda.api.http.routing.HTTPPostRoute[$tpe] {
      def apply(requestContext: LambdaRequestContext): LambdaHTTPResponse = {

        $block(requestContext)
      }
    }

    addRoute(com.virtuslab.zeitgeist.lambda.api.http.http.POST, $route, handler)
    """

    c.Expr[Any](obj)
  }

  def post[T](route: String)(block: RouteBlockWithParams): Any = macro post_impl[T]

  def post_impl[T : c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context)(route: c.Expr[String])
                                             (block: c.Expr[RouteBlockWithParams]): c.Expr[Any] = {
    import c.universe._

    val tpe = weakTypeOf[T]

    if (tpe =:= typeOf[Nothing])
      c.abort(c.enclosingPosition, "POST routes require a case class argument describing how the JSON body should be deserialized. Please call it as `post[<TYPE>](<route>) ...`")

    val obj = q"""
    val handler = new com.virtuslab.zeitgeist.lambda.api.http.routing.HTTPPostRoute[$tpe] {
      def apply(requestContext: LambdaRequestContext): LambdaHTTPResponse = {
        $block(requestContext)
      }
    }

    addRoute(com.virtuslab.zeitgeist.lambda.api.http.http.POST, $route, handler)
    """

    c.Expr[Any](obj)
  }

  def put[T <: Product](route: String)(block: RouteBlockWithParams): Any = macro put_impl[T]

  def put_impl[T <: Product : c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context)(route: c.Expr[String])
                                            (block: c.Expr[RouteBlockWithParams]): c.Expr[Any] = {
    import c.universe._

    val tpe = weakTypeOf[T]

    if (tpe =:= typeOf[Nothing])
      c.abort(c.enclosingPosition, "PUT routes require a case class argument describing how the JSON body should be deserialized. Please call it as `put[<TYPE>](<route>) ...`")

    val obj = q"""
    val handler = new com.virtuslab.zeitgeist.lambda.api.http.routing.HTTPPutRoute[$tpe] {
      def apply(requestContext: LambdaRequestContext): LambdaHTTPResponse = {
        $block(requestContext)
      }
    }

    addRoute(com.virtuslab.zeitgeist.lambda.api.http.http.PUT, $route, handler)
    """

    c.Expr[Any](obj)
  }

  def deleteWithBody[T <: Product](route: String)(block: RouteBlock[T]): Any = macro delete_with_body_impl[T]


  def delete_with_body_impl[T <: Product : c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context)(route: c.Expr[String])(block: c.Expr[T ⇒ LambdaHTTPResponse]): c.Expr[Any] = {
    import c.universe._

    val tpe = weakTypeOf[T]

    val obj = q"""
        val handler = new com.virtuslab.zeitgeist.lambda.api.http.routing.HTTPPostRoute[$tpe] {
          def apply(): LambdaHTTPResponse = {
            val body = request.body.extract[$tpe]
            $block(body)
          }
        }

        addRoute(com.virtuslab.zeitgeist.lambda.api.http.http.DELETE, $route, handler)
        """

    c.Expr[Any](obj)
  }

  def delete(route: String)(block: RouteBlockWithParams): Any = macro delete_typeless_impl

  def delete_typeless_impl(c: scala.reflect.macros.whitebox.Context)(route: c.Expr[String])(block: c.Expr[RouteBlockWithParams]): c.Expr[Any] = {
    import c.universe._

/*
      c.warning(
        c.enclosingPosition, "DELETE Route invoked with no type argument. " +
          "the type of the `body` will be Unit. If you didn't mean to do this, please " +
          "ensure to call delete with a type argument, such as `delete[T](<route>)...`, where T is a case class."
      )
*/

    val obj = q"""
        val handler = new com.virtuslab.zeitgeist.lambda.api.http.routing.HTTPDeleteRoute[Nothing] {
          def apply(requestContext: LambdaRequestContext): LambdaHTTPResponse = {
            $block(requestContext)
          }
        }

        addRoute(com.virtuslab.zeitgeist.lambda.api.http.http.DELETE, $route, handler)
        """

    c.Expr[Any](obj)
  }

  def patch[T <: Product](route: String)(block: RouteBlockWithParams): Any = macro patch_impl[T]

  def patch_impl[T <: Product : c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context)(route: c.Expr[String])
                                              (block: c.Expr[RouteBlockWithParams]): c.Expr[Any] = {
    import c.universe._

    val tpe = weakTypeOf[T]

    if (tpe =:= typeOf[Nothing])
      c.abort(c.enclosingPosition, "PATCH routes require a case class argument describing how the JSON body should be deserialized. Please call it as `patch[<TYPE>](<route>) ...`")

    val obj = q"""
    val handler = new com.virtuslab.zeitgeist.lambda.api.http.routing.HTTPPostRoute[$tpe] {
      def apply(requestContext: LambdaRequestContext): LambdaHTTPResponse = {
        $block(requestContext)
      }
    }

    addRoute(com.virtuslab.zeitgeist.lambda.api.http.http.PATCH, $route, handler)
    """

    c.Expr[Any](obj)
  }
}
