package pillars.http.server

import cats.effect.IO
import cats.effect.Resource
import org.http4s.HttpApp
import org.http4s.netty.server.NettyServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.ErrorHandling
import org.http4s.server.middleware.Logger
import pillars.config.HttpServerConfig
import pillars.http.HttpEndpoint
import pillars.observability.Observability
import pillars.utils.*
import sttp.tapir.*
import sttp.tapir.server.http4s.Http4sServerInterpreter

object HttpServer:
  def build(
      name: String,
      config: HttpServerConfig,
      observability: Observability[IO],
      endpoints: List[HttpEndpoint]
  ): Resource[IO, Server] =
    val cors: HttpApp[IO] => HttpApp[IO] =
      CORS.policy.withAllowMethodsAll.withAllowOriginAll.withAllowHeadersAll.httpApp[IO]
    val errorHandling: HttpApp[IO] => HttpApp[IO] = ErrorHandling.httpApp[IO]
    val logging = Logger.httpApp[IO](
      logHeaders = false,
      logBody = true,
      redactHeadersWhen = _ => false,
      logAction = Some(scribe.cats.io.debug(_))
    )
    val app: HttpApp[IO] =
      Http4sServerInterpreter[IO]()
        .toRoutes(endpoints)
        .orNotFound |>
        logging |>
        errorHandling |>
        cors

    NettyServerBuilder[IO].withoutSsl.withNioTransport
      .bindHttp(config.port.value, config.host.toString)
      .withHttpApp(app)
      .withoutBanner
      .resource
