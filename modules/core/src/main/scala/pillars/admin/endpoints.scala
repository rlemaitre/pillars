package pillars.admin

import cats.effect.IO
import cats.syntax.all.*
import pillars.http.HttpEndpoint
import sttp.tapir.*
object endpoints:
  val liveness: HttpEndpoint     = endpoint.get.in("healthz").out(stringBody).serverLogicSuccess(_ => "OK".pure[IO])
  val readiness: HttpEndpoint    = endpoint.get.in("health").out(stringBody).serverLogicSuccess(_ => "OK".pure[IO])
  val probes: List[HttpEndpoint] = List(liveness, readiness)
  val all: List[HttpEndpoint]    = probes
