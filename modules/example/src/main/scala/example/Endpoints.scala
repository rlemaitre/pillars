// Copyright (c) 2024-2024 by Raphaël Lemaitre and Contributors
// This software is licensed under the Eclipse Public License v2.0 (EPL-2.0).
// For more information see LICENSE or https://opensource.org/license/epl-2-0

package example

import cats.syntax.all.*
import example.codec.json.given
import pillars.PillarsError
import sttp.tapir.*
import sttp.tapir.codec.iron.given
import sttp.tapir.json.circe.jsonBody

object Endpoints:
    private val base                                               = endpoint.errorOut(jsonBody[PillarsError.View])
    val ping: Endpoint[Unit, Unit, PillarsError.View, String, Any] =
        base
            .get
            .in("ping")
            .name("ping")
            .description("Always return pong")
            .out(stringBody)

    val boom: Endpoint[Unit, Unit, PillarsError.View, String, Any] =
        base
            .get
            .in("boom")
            .name("boom")
            .description("Always in error")
            .out(stringBody)

    private val api      = endpoint.in("v0").errorOut(jsonBody[PillarsError.View])
    private val userBase = api.in("user")

    val createUser: Endpoint[Unit, UserView, PillarsError.View, UserView, Any] = userBase
        .in(jsonBody[UserView])
        .put
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
      ping,
      createUser,
      listUser,
      getUser,
      deleteUser
    )
end Endpoints
