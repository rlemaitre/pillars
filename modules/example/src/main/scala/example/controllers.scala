package example

import cats.effect.IO
import cats.syntax.all.*
import pillars.Controller
import pillars.Controller.HttpEndpoint
import pillars.Pillars

def homeController(using Pillars[IO]): Controller[IO] =
    def ping: HttpEndpoint[IO] = Endpoints.ping.serverLogicSuccess: _ =>
        "pong".pure[IO]
    def boom: HttpEndpoint[IO] = Endpoints.boom.serverLogic: _ =>
        throw new RuntimeException("💣 boom")

    List(ping, boom)
end homeController

def userController(using Pillars[IO]): Controller[IO] =
    def list: HttpEndpoint[IO] = Endpoints.listUser.serverLogic: _ =>
        Left(errors.api.NotImplemented.view).pure[IO]

    def create: HttpEndpoint[IO] = Endpoints.createUser.serverLogic: _ =>
        Left(errors.api.NotImplemented.view).pure[IO]

    def get: HttpEndpoint[IO] = Endpoints.getUser.serverLogic: _ =>
        Left(errors.api.NotImplemented.view).pure[IO]

    def delete: HttpEndpoint[IO] = Endpoints.deleteUser.serverLogic: _ =>
        Left(errors.api.NotImplemented.view).pure[IO]

    List(list, create, get, delete)
end userController
