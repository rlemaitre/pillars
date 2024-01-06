package pillars.admin

import cats.effect.IO
import cats.effect.kernel.Resource.ExitCase
import pillars.config.AdminConfig
import pillars.http.server.HttpServer
import pillars.observability.Observability
import scribe.cats.io.*

final case class AdminServer(config: AdminConfig, obs: Observability[IO]):
  def start(): IO[Unit] =
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
    else IO.unit
