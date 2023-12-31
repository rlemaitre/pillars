package pillars.http.server

import cats.data.Kleisli
import cats.data.OptionT
import cats.effect.Clock
import cats.effect.IO
import cats.effect.Resource
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.Method
import org.http4s.Request
import org.http4s.Response
import org.http4s.Uri
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.ErrorHandling
import pillars.api.APIError
import pillars.config.HttpServerConfig
import scala.concurrent.duration.FiniteDuration
import scala.util.chaining.*
import scribe.Level
import scribe.Logger
import scribe.cats.io.*
import scribe.mdc.MDC
import sttp.capabilities.fs2.Fs2Streams
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sDefaultServerLog
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.server.http4s.Http4sServerOptions
import sttp.tapir.server.interceptor.exception.ExceptionHandler
import sttp.tapir.server.interceptor.metrics.MetricsRequestInterceptor
import sttp.tapir.server.model.ValuedEndpointOutput

object HttpServer:

  /** The application exposes all endpoints on `/` directly. But the load balancer prefixes all urls. The prefix is used
   * to fix API paths in the OpenAPI definition. Without the prefix, the SwaggerUI would be broken This is not ideal
   * because a change in load balancer may need a fix in the code. Would be nice if SwaggerUI was able to make call
   * relative to its path, but I don't think it's possible.
   *
   * @param config
   *   The config
   * @param endpoints
   *   The exposed endpoints
   * @param metricsInterceptor
   *   Metric interceptor
   *
   * @return
   */

  def build(
      name: String,
      config: HttpServerConfig,
      endpoints: List[ServerEndpoint[Fs2Streams[IO], IO]],
      metricsInterceptor: Option[MetricsRequestInterceptor[IO]],
      contextPath: Uri.Path
  ): Resource[IO, Server] =
    val options = buildInterceptors(metricsInterceptor)

    val cors = CORS.policy.withAllowOriginAll.withAllowHeadersAll
      .withAllowMethodsIn(Set(Method.GET, Method.POST, Method.OPTIONS))
      .httpApp[IO]

    val errorHandling = ErrorHandling.httpApp[IO]
    val logging: HttpRoutes[IO] => HttpRoutes[IO] =
      def action(time: FiniteDuration, req: Request[IO], res: Response[IO], level: Level)(using mdc: MDC): IO[Unit] =
        log(level, mdc, s"${req.method.name} ${req.uri.renderString} ${res.status.code} ${time.toMillis}ms")

      routes =>
        Kleisli: req =>
          val timed: OptionT[IO, (FiniteDuration, Response[IO])] = Clock.clockForOptionT.timed(routes.run(req))
          timed
            .semiflatTap:
              case (time, res) =>
                if res.status.isSuccess && Logger.root.includes(Level.Debug) then action(time, req, res, Level.Debug)
                else if !res.status.isSuccess then action(time, req, res, Level.Error)
                else IO.unit
            .map:
              case (_, res) => res

    val interpreter = Http4sServerInterpreter[IO](options)

    val removeContextPath: HttpRoutes[IO] => HttpRoutes[IO] = routes =>
      if (contextPath.isEmpty) routes
      else
        Kleisli: req =>
          val oldPath             = req.uri.path
          val isPrefixed: Boolean = oldPath.segments.startsWith(contextPath.segments)

          if (isPrefixed)
            val newPath = Uri.Path(
              segments = oldPath.segments.drop(contextPath.segments.length),
              absolute = oldPath.absolute,
              endsWithSlash = oldPath.endsWithSlash
            )
            routes.run(req.withPathInfo(newPath))
          else routes.run(req)

    val app = endpoints
      .pipe(interpreter.toRoutes)
      .pipe(logging)
      .pipe(removeContextPath)
      .orNotFound
      .pipe(errorHandling)
      .pipe(cors)

    EmberServerBuilder
      .default[IO]
      .withHost(config.host)
      .withPort(config.port)
      .withMaxConnections(config.maxConnections)
      .withHttpApp(app)
      .build
      .evalTap(server => info(s"Started $name HTTP Server on ${server.baseUri}"))

  private def buildInterceptors(
      metricsInterceptor: Option[MetricsRequestInterceptor[IO]],
      enableDefaultLogging: Boolean = true
  ): Http4sServerOptions[IO] =
    val endpointOutput: EndpointOutput[(StatusCode, String)] = statusCode.and(stringBody)

    val exceptionHandler: ExceptionHandler[IO] = ExceptionHandler.apply[IO](ctx =>
      OptionT.pure:
        ctx.e match
          case e: APIError => ValuedEndpointOutput(endpointOutput, (e.statusCode, e.getMessage))
          case _ => ValuedEndpointOutput(endpointOutput, (StatusCode.InternalServerError, "Internal server error"))
      .value
    )

    Http4sServerOptions
      .customiseInterceptors[IO]
      .exceptionHandler(exceptionHandler)
      .metricsInterceptor(metricsInterceptor)
      .serverLog(Option.when(enableDefaultLogging)(Http4sDefaultServerLog[IO]))
      .options
