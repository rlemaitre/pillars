package example

import cats.effect.IO
import cats.syntax.all.*
import pillars.{Controller, Pillars}
import pillars.Controller.HttpEndpoint
import sttp.tapir.*

final case class TodoController()(using p: Pillars[IO]) extends Controller[IO]:
    def list: HttpEndpoint[IO] = endpoint.get.out(stringBody).serverLogicSuccess(_ => "OK".pure[IO])
    val endpoints = List(list)
end TodoController

object TodoController:
  def apply()(using p: Pillars[IO]): TodoController = new TodoController()
end TodoController
