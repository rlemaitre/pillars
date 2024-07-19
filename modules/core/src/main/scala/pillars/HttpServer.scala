package pillars

import cats.Applicative
import cats.effect.Async
import cats.effect.Resource
import cats.syntax.all.*
import com.comcast.ip4s.*
import io.circe.Codec
import io.circe.derivation.Configuration
import pillars.syntax.*
import org.http4s.HttpApp
import org.http4s.HttpVersion
import org.http4s.Response
import org.http4s.Status
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.netty.server.NettyServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.ErrorHandling
import org.http4s.server.middleware.Logger
import pillars.Controller.HttpEndpoint
import pillars.codec.given
import sttp.capabilities.StreamMaxLengthExceededException
import sttp.monad.MonadError
import sttp.tapir.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.server.http4s.Http4sServerOptions
import sttp.tapir.server.interceptor.exception.ExceptionContext
import sttp.tapir.server.interceptor.exception.ExceptionHandler
import sttp.tapir.server.model.ValuedEndpointOutput

object HttpServer:
    def build[F[_]: Async](
        name: String,
        config: Config,
        observability: Observability[F],
        endpoints: List[HttpEndpoint[F]]
    ): Resource[F, Server] =
        val cors: HttpApp[F] => HttpApp[F]          =
            CORS.policy.withAllowMethodsAll.withAllowOriginAll.withAllowHeadersAll.httpApp[F]
        val errorHandling: HttpApp[F] => HttpApp[F] = ErrorHandling.Custom.recoverWith(_)(buildExceptionHandler())
        val logging                                 =
            if config.logging.enabled then
                Logger.httpApp[F](
                  logHeaders = config.logging.headers,
                  logBody = config.logging.body,
                  logAction = config.logging.logAction
                )
            else identity[HttpApp[F]]

        val options: Http4sServerOptions[F] =
            Http4sServerOptions
                .customiseInterceptors
                .prependInterceptor(observability.interceptor)
                .exceptionHandler(exceptionHandler())
                .options

        val routes = Http4sServerInterpreter[F](options).toRoutes(endpoints).orNotFound
        val app: HttpApp[F] = routes |> logging |> errorHandling |> cors

        NettyServerBuilder[F].withoutSsl.withNioTransport
            .bindHttp(config.port.value, config.host.toString)
            .withHttpApp(app)
            .withoutBanner
            .resource
    end build

    private def exceptionHandler[F[_]](): ExceptionHandler[F] =
        new ExceptionHandler[F]:
            override def apply(ctx: ExceptionContext)(implicit
                monad: MonadError[F]
            ): F[Option[ValuedEndpointOutput[_]]] =
                def handlePillarsError(e: PillarsError) =
                    Some(ValuedEndpointOutput(statusCode.and(jsonBody[PillarsError.View]), (e.status, e.view)))

                ctx.e match
                case e: PillarsError                            =>
                    monad.unit(handlePillarsError(e))
                case StreamMaxLengthExceededException(maxBytes) =>
                    monad.unit(handlePillarsError(PillarsError.PayloadTooLarge(maxBytes)))
                case _                                          =>
                    monad.unit(handlePillarsError(PillarsError.fromThrowable(ctx.e)))
                end match
            end apply

    private def buildExceptionHandler[F[_]: Applicative](): PartialFunction[Throwable, F[Response[F]]] =
        case e: PillarsError =>
            Response(
              Status.fromInt(e.status.code).getOrElse(Status.InternalServerError),
              HttpVersion.`HTTP/1.1`
            )
                .withEntity(e.view)
                .pure[F]
        case e: Throwable    =>
            Response(Status.InternalServerError, HttpVersion.`HTTP/1.1`)
                .withEntity(PillarsError.fromThrowable(e).view)
                .pure[F]
    end buildExceptionHandler

    final case class Config(
        host: Host,
        port: Port,
        logging: Logging.HttpConfig = Logging.HttpConfig() 
    )

    object Config:
        given Configuration = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults

        given Codec[Config] = Codec.AsObject.derivedConfigured

    end Config
end HttpServer
