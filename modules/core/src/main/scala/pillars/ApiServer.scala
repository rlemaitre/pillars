package pillars

import cats.effect.Async
import cats.effect.Resource.ExitCase
import cats.syntax.all.*
import com.comcast.ip4s.*
import io.circe.Codec
import io.github.iltotore.iron.*
import pillars.Controller.HttpEndpoint
import pillars.PillarsError.Code
import scribe.Scribe
import sttp.model.StatusCode

trait ApiServer[F[_]]:
    def start(endpoints: List[HttpEndpoint[F]]): F[Unit]

object ApiServer:
    def apply[F[_]]: Run[F, ApiServer[F]]                                                                   = summon[Pillars[F]].apiServer
    def init[F[_]: Async](config: Config, observability: Observability[F], logger: Scribe[F]): ApiServer[F] =
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
    trait Error extends PillarsError:
        override def status: StatusCode
        final override def code: Code = Code("API")
    end Error

    final case class Config(enabled: Boolean, http: HttpServer.Config = defaultHttp) derives Codec.AsObject

    private val defaultHttp = HttpServer.Config(host = host"0.0.0.0", port = port"9876", enableLogging = false)
end ApiServer
