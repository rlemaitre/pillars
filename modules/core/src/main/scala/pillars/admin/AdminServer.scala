package pillars.admin

import cats.effect.Async
import cats.effect.Resource.ExitCase
import cats.effect.kernel.Async
import cats.syntax.all.*
import pillars.config.AdminConfig
import pillars.http.server.HttpServer
import pillars.observability.Observability

final case class AdminServer[F[_]: Async](config: AdminConfig, obs: Observability[F]):
  def start(): F[Unit] =
    val logger = scribe.cats.effect[F]
    import logger.*
    if config.enabled then
      for
        _ <- info(s"Starting admin server on ${config.http.host}:${config.http.port}")
        _ <- HttpServer
          .build("admin", config.http, obs, endpoints.all)
          .onFinalizeCase:
            case ExitCase.Errored(e) => error(s"Admin server stopped with error: $e")
            case _                   => info("Admin server stopped")
          .useForever
      yield ()
    else Async[F].unit
