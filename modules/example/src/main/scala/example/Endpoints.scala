package example

import cats.syntax.all.*
import example.codec.json.given
import pillars.PillarsError
import sttp.tapir.*
import sttp.tapir.codec.iron.given
import sttp.tapir.json.circe.jsonBody

object Endpoints:
    val home: Endpoint[Unit, Unit, Unit, String, Any] = endpoint.get.out(stringBody)

    private val base     = endpoint.in("v0").errorOut(jsonBody[PillarsError.View])
    private val userBase = base.in("user")

    val createUser: Endpoint[Unit, UserView, PillarsError.View, UserView, Any] = userBase
        .in(jsonBody[UserView])
        .post
        .out(jsonBody[UserView])
        .name("create user")

    val listUser: Endpoint[Unit, Unit, PillarsError.View, List[UserView], Any] = userBase
        .get
        .out(jsonBody[List[UserView]])
        .name("list user")

    val getUser: Endpoint[Unit, Email, PillarsError.View, UserView, Any] = userBase
        .in(path[Email].name("e-mail"))
        .get
        .out(jsonBody[UserView])
        .name("get user")

    val deleteUser: Endpoint[Unit, Email, PillarsError.View, UserView, Any] = userBase
        .in(path[Email].name("e-mail"))
        .delete
        .out(jsonBody[UserView])
        .name("delete user")

    val all = List(
      home,
      createUser,
      listUser,
      getUser,
      deleteUser
    )
end Endpoints
