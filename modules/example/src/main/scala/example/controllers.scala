package example

import cats.effect.IO
import cats.syntax.all.*
import pillars.Controller
import pillars.Controller.HttpEndpoint
import pillars.Logger
import pillars.Pillars
import pillars.db.DB
import skunk.implicits.sql

def homeController(using p: Pillars[IO]): Controller[IO] =
    def ping: HttpEndpoint[IO] = Endpoints.ping.serverLogicSuccess: _ =>
        p.observability.tracer.span("ping").surround:
            "pong".pure[IO]
    def boom: HttpEndpoint[IO] = Endpoints.boom.serverLogic: _ =>
        throw new RuntimeException("💣 boom")

    List(ping, boom)
end homeController

def userController(using Pillars[IO]): Controller[IO] =
    def list: HttpEndpoint[IO] = Endpoints.listUser.serverLogic: _ =>
        Left(errors.api.NotImplemented.view).pure[IO]

    def create: HttpEndpoint[IO] = Endpoints.createUser.serverLogic: user =>
        DB[IO].use: session =>
            for
                completion <- session.execute(db.users.createUser)(user.toModel)
                _          <- Logger[IO].debug(s"Create user resulted in $completion.")
            yield Right(user)

    def get: HttpEndpoint[IO] = Endpoints.getUser.serverLogic: _ =>
        Left(errors.api.NotImplemented.view).pure[IO]

    def delete: HttpEndpoint[IO] = Endpoints.deleteUser.serverLogic: _ =>
        Left(errors.api.NotImplemented.view).pure[IO]

    List(list, create, get, delete)
end userController
