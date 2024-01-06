package pillars.admin

import cats.Monad
import pillars.http.HttpEndpoint
import sttp.tapir.*
object endpoints:
  def liveness[F[_]: Monad]: HttpEndpoint[F] =
    endpoint.get.in("healthz").out(stringBody).serverLogicSuccess(_ => Monad[F].pure("OK"))
  def readiness[F[_]: Monad]: HttpEndpoint[F] =
    endpoint.get.in("health").out(stringBody).serverLogicSuccess(_ => Monad[F].pure("OK"))
  def probes[F[_]: Monad]: List[HttpEndpoint[F]] = List(liveness, readiness)
  def all[F[_]: Monad]: List[HttpEndpoint[F]]    = probes
