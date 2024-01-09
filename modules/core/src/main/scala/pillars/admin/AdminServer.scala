package pillars.admin

import cats.Applicative
import cats.effect.Async
import cats.effect.Resource.ExitCase
import cats.effect.kernel.Async
import cats.syntax.all.*
import pillars.admin.AdminServer.liveness
import pillars.config.AdminConfig
import pillars.http.server.Controller
import pillars.http.server.Controller.HttpEndpoint
import pillars.http.server.HttpServer
import pillars.observability.Observability

final case class AdminServer[F[_]: Async](
    config: AdminConfig,
    obs: Observability[F],
    controllers: List[Controller[F]] = List.empty[Controller[F]]
):
  def start(): F[Unit] =
    val logger = scribe.cats.effect[F]
    import logger.*
    if config.enabled then
      for
        _ <- info(s"Starting admin server on ${config.http.host}:${config.http.port}")
        _ <- HttpServer
          .build("admin", config.http, obs, controllers.foldLeft(List(liveness))(_ ++ _.endpoints))
          .onFinalizeCase:
            case ExitCase.Errored(e) => error(s"Admin server stopped with error: $e")
            case _                   => info("Admin server stopped")
          .useForever
      yield ()
    else Async[F].unit
object AdminServer:
  private def liveness[F[_]: Applicative]: HttpEndpoint[F] =
    endpoints.probes.liveness.serverLogicSuccess(_ => "OK".pure[F])
