package example

import cats.effect.IO
import cats.syntax.all.*
import pillars.Controller
import pillars.Controller.HttpEndpoint
import pillars.Pillars

final case class HomeController()(using Pillars[IO]) extends Controller[IO]:
    def list: HttpEndpoint[IO] = Endpoints.home.serverLogicSuccess: _ =>
        "👋 Hi".pure[IO]
    val endpoints              = List(list)
end HomeController

final case class UserController()(using Pillars[IO]) extends Controller[IO]:
    def list: HttpEndpoint[IO] = Endpoints.listUser.serverLogic: _ =>
        Left(errors.api.NotImplemented.view).pure[IO]

    def create: HttpEndpoint[IO] = Endpoints.createUser.serverLogic: _ =>
        Left(errors.api.NotImplemented.view).pure[IO]

    def get: HttpEndpoint[IO] = Endpoints.getUser.serverLogic: _ =>
        Left(errors.api.NotImplemented.view).pure[IO]

    def delete: HttpEndpoint[IO] = Endpoints.deleteUser.serverLogic: _ =>
        Left(errors.api.NotImplemented.view).pure[IO]

    val endpoints = List(list, create, get, delete)
end UserController
