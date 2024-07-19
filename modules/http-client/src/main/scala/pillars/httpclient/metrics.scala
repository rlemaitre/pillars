package pillars.httpclient

import cats.effect.Async
import cats.effect.Clock
import cats.effect.Resource
import cats.effect.syntax.all.*
import cats.syntax.all.*
import org.http4s.Request
import org.http4s.Response
import org.http4s.Status
import org.http4s.client.Client
import org.typelevel.otel4s.Attribute
import org.typelevel.otel4s.metrics.BucketBoundaries
import org.typelevel.otel4s.metrics.Histogram
import org.typelevel.otel4s.metrics.Meter
import org.typelevel.otel4s.metrics.UpDownCounter
import pillars.Observability
import pillars.Observability.*

final case class MetricsCollection[F[_]](
    responseDuration: Histogram[F, Long],
    activeRequests: UpDownCounter[F, Long],
    requestBodySize: Histogram[F, Long],
    responseBodySize: Histogram[F, Long]
)

object MetricsCollection:
    def create[F[_]: Async](meter: Meter[F]): F[MetricsCollection[F]] =
        (
          meter.histogram[Long]("http.client.request.duration")
              .withUnit("ms")
              .withDescription("Response time in milliseconds")
              .withExplicitBucketBoundaries(
                BucketBoundaries(Vector(5L, 10L, 25L, 50L, 75L, 100L, 250L, 500L, 750L, 1000L, 2500L, 5000L, 7500L,
                  10000L))
              )
              .create,
          meter
              .upDownCounter[Long]("http.client.active_requests")
              .withUnit("{request}")
              .withDescription("Active requests")
              .create,
          meter.histogram[Long]("http.client.request.body.size")
              .withUnit("By")
              .withDescription(
                "The size of the request payload body in bytes. This is the number of bytes transferred excluding headers and is often, but not always, present as the Content-Length header."
              )
              .create,
          meter.histogram[Long]("http.client.response.body.size")
              .withUnit("By")
              .withDescription(
                "The size of the response payload body in bytes. This is the number of bytes transferred excluding headers and is often, but not always, present as the Content-Length header."
              )
              .create
        ).mapN(MetricsCollection.apply)
end MetricsCollection

object ClientMetrics:
    def apply[F[_]: Async](observability: Observability[F]): F[ClientMetrics[F]] =
        MetricsCollection.create(observability.metrics).map(ClientMetrics.apply)

final case class ClientMetrics[F[_]](metrics: MetricsCollection[F])(using async: Async[F], clock: Clock[F]):

    def middleware(client: Client[F]): Client[F] =
        Client(instrument(client))

    private def instrument(client: Client[F])(request: Request[F]): Resource[F, Response[F]] =
        val requestAttributes = extractAttributes(request)
        clock.monotonic.toResource.flatMap { start =>
            val happyPath: Resource[F, Response[F]] =
                for
                    _                 <- Resource.make(
                                           metrics.activeRequests.inc(requestAttributes*)
                                         )(_ =>
                                             metrics.activeRequests.dec(requestAttributes*)
                                         )
                    response          <- client.run(request)
                    end               <- Resource.eval(clock.monotonic)
                    responseAttributes = extractAttributes(response)
                    _                 <- Resource.eval(metrics.responseDuration.record(
                                           (end - start).toMillis,
                                           responseAttributes ++ responseAttributes*
                                         ))
                yield response
            happyPath.handleErrorWith: (e: Throwable) =>
                Resource.eval:
                    clock.monotonic
                        .flatMap(now =>
                            metrics.responseDuration.record(
                              (now - start).toMillis,
                              requestAttributes ++ extractAttributes(e)*
                            )
                        )
                        .flatMap(_ =>
                            async.raiseError[Response[F]](e)
                        )
        }
    end instrument

    private def extractAttributes(value: Request[F] | Response[F] | Throwable) =
        val l = value match
        case request: Request[F]   =>
            List(
              "http.route"          -> s"${request.uri.host.map(_.value).getOrElse("")}",
              "http.request.method" -> request.method.name,
              "url.scheme"          -> request.uri.scheme.map(_.value).getOrElse("")
            )
        case response: Response[F] =>
            List(
              "http.response.status"      -> {
                  response.status.responseClass match
                  case Status.Informational => "1xx"
                  case Status.Successful    => "2xx"
                  case Status.Redirection   => "3xx"
                  case Status.ClientError   => "4xx"
                  case Status.ServerError   => "5xx"
              },
              "http.response.status_code" -> response.status.code.toString
            )
        case e: Throwable          =>
            List(
              "error.type" -> e.getClass.getName
            )
        l.map { case (name, value) => value.toAttribute(name) }
    end extractAttributes

end ClientMetrics
