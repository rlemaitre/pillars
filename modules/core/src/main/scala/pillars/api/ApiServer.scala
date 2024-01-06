package pillars.api

import cats.effect.IO
import cats.effect.Resource.ExitCase
import pillars.config.ApiConfig
import pillars.http.HttpEndpoint
import pillars.http.server.HttpServer
import pillars.observability.Observability
import scribe.cats.io.*

trait ApiServer:
  def start(endpoints: List[HttpEndpoint]): IO[Unit]

object ApiServer:
  def init(config: ApiConfig, observability: Observability[IO]): ApiServer =
    (endpoints: List[HttpEndpoint]) =>
      if config.enabled then
        for
          _ <- info(s"Starting API server on ${config.http.host}:${config.http.port}")
          _ <- HttpServer
            .build("api", config.http, observability, endpoints)
            .onFinalizeCase:
              case ExitCase.Errored(e) => error(s"API server stopped with error: $e")
              case _                   => info("API server stopped")
            .useForever
        yield ()
      else IO.unit
