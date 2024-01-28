package pillars

import cats.effect.Async
import cats.effect.Resource
import com.comcast.ip4s.*
import io.circe.Codec
import io.circe.derivation.Configuration
import mouse.all.anySyntaxMouse
import org.http4s.HttpApp
import org.http4s.netty.server.NettyServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.ErrorHandling
import org.http4s.server.middleware.Logger
import pillars.Controller.HttpEndpoint
import pillars.codec.given
import sttp.tapir.server.http4s.Http4sServerInterpreter

object HttpServer:
    def build[F[_]: Async](
        name: String,
        config: Config,
        observability: Observability[F],
        endpoints: List[HttpEndpoint[F]]
    ): Resource[F, Server] =
        val cors: HttpApp[F] => HttpApp[F]          =
            CORS.policy.withAllowMethodsAll.withAllowOriginAll.withAllowHeadersAll.httpApp[F]
        val errorHandling: HttpApp[F] => HttpApp[F] = ErrorHandling.httpApp[F]
        val logging                                 = Logger.httpApp[F](
          logHeaders = false,
          logBody = true,
          redactHeadersWhen = _ => false,
          logAction = Some(scribe.cats[F].debug(_))
        )
        val app: HttpApp[F]                         =
            Http4sServerInterpreter[F]()
                .toRoutes(endpoints)
                .orNotFound |>
                logging |>
                errorHandling |>
                cors

        NettyServerBuilder[F].withoutSsl.withNioTransport
            .bindHttp(config.port.value, config.host.toString)
            .withHttpApp(app)
            .withoutBanner
            .resource
    end build
    final case class Config(
        host: Host,
        port: Port,
        enableLogging: Boolean
    )

    object Config:
        given Configuration = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults

        given Codec[Config] = Codec.AsObject.derivedConfigured

    end Config
end HttpServer
