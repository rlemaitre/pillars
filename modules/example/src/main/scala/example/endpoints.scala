package example

import cats.effect.IO
import cats.syntax.all.*
import pillars.http.server.Controller.HttpEndpoint
import sttp.tapir.*

object endpoints:
    def all: List[HttpEndpoint[IO]] = endpoint.get.out(stringBody).serverLogicSuccess(_ => "OK".pure[IO]) :: Nil
