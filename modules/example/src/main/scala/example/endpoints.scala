package example

import cats.effect.IO
import cats.syntax.all.*
import pillars.Controller
import pillars.Controller.HttpEndpoint
import sttp.tapir.*

final case class TodoController() extends Controller[IO]:
    def list: HttpEndpoint[IO] = endpoint.get.out(stringBody).serverLogicSuccess(_ => "OK".pure[IO])
    val endpoints              = List(list)
end TodoController
