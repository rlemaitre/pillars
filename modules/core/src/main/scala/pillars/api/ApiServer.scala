package pillars.api

import cats.effect.Async
import cats.effect.Resource.ExitCase
import cats.syntax.all.*
import pillars.config.ApiConfig
import pillars.http.server.Controller.HttpEndpoint
import pillars.http.server.HttpServer
import pillars.observability.Observability
import scribe.Scribe

trait ApiServer[F[_]]:
    def start(endpoints: List[HttpEndpoint[F]]): F[Unit]

object ApiServer:
    def init[F[_]: Async](config: ApiConfig, observability: Observability[F], logger: Scribe[F]): ApiServer[F] =
        (endpoints: List[HttpEndpoint[F]]) =>
            Async[F].whenA(config.enabled):
                for
                    _ <- logger.info(s"Starting API server on ${config.http.host}:${config.http.port}")
                    _ <- HttpServer
                             .build("api", config.http, observability, endpoints)
                             .onFinalizeCase:
                                 case ExitCase.Errored(e) => logger.error(s"API server stopped with error: $e")
                                 case _                   => logger.info("API server stopped")
                             .useForever
                yield ()
end ApiServer
