package pillars

import cats.Applicative
import cats.Monad
import cats.syntax.all.*
import java.time.Duration
import java.time.Instant
import org.typelevel.otel4s.Attribute
import org.typelevel.otel4s.AttributeKey
import org.typelevel.otel4s.metrics.BucketBoundaries
import org.typelevel.otel4s.metrics.Counter
import org.typelevel.otel4s.metrics.Histogram
import org.typelevel.otel4s.metrics.Meter
import org.typelevel.otel4s.metrics.UpDownCounter
import sttp.monad.MonadError
import sttp.tapir.AnyEndpoint
import sttp.tapir.model.ServerRequest
import sttp.tapir.server.interceptor.metrics.MetricsRequestInterceptor
import sttp.tapir.server.metrics.EndpointMetric
import sttp.tapir.server.metrics.Metric
import sttp.tapir.server.metrics.MetricLabels
import sttp.tapir.server.model.ServerResponse

case class Otel4sMetrics[F[_]: Applicative](meter: Meter[F], metrics: List[Metric[F, _]]):
    /** The interceptor which can be added to a server's options, to enable metrics collection. */
    def metricsInterceptor(ignoreEndpoints: Seq[AnyEndpoint] = Seq.empty): MetricsRequestInterceptor[F] =
        new MetricsRequestInterceptor[F](metrics, ignoreEndpoints)
end Otel4sMetrics

object Otel4sMetrics:

    private lazy val labels: MetricLabels = MetricLabels(
      forRequest = List(
        "http.route"          -> { case (ep, _) => ep.showPathTemplate(showQueryParam = None) },
        "http.request.method" -> { case (_, req) => req.method.method },
        "url.scheme"          -> { case (_, req) => req.uri.scheme.getOrElse("") }
      ),
      forResponse = List(
        "http.response.status"      -> {
            case Right(r) =>
                r.code match
                    case c if c.isInformational => "1xx"
                    case c if c.isSuccess       => "2xx"
                    case c if c.isRedirect      => "3xx"
                    case c if c.isClientError   => "4xx"
                    case c if c.isServerError   => "5xx"
                    case _                      => ""
            case Left(_)  => "5xx"
        },
        "http.response.status_code" -> {
            case Right(r) => r.code.toString
            case Left(_)  => "500"
        },
        "error.type"                -> {
            case Left(ex: PillarsError) => ex.code
            case Left(ex)               => ex.getClass.getName
            case _                      => ""
        }
      )
    )

    /** Using the default labels, registers the following metrics:
    *
    *   - `request_active{path, method}` (up-down-counter)
    *   - `request_total{path, method, status}` (counter)
    *   - `request_duration{path, method, status, phase}` (histogram)
    *
    * Status is by default the status code class (1xx, 2xx, etc.), and phase can be either `headers` or `body` - request duration is
    * measured separately up to the point where the headers are determined, and then once again when the whole response body is complete.
    */
    def init[F[_]: Monad](meter: Meter[F]): F[Otel4sMetrics[F]] =
        for
            active       <- requestActive(meter)
            total        <- requestTotal(meter)
            duration     <- requestDuration(meter)
            requestSize  <- requestBodySize(meter)
            responseSize <- responseBodySize(meter)
        yield Otel4sMetrics(meter, List[Metric[F, _]](active, total, duration))

    def init[F[_]: Applicative](meter: Meter[F], metrics: List[Metric[F, _]]): F[Otel4sMetrics[F]] =
        Otel4sMetrics(meter, metrics).pure[F]

    def noop[F[_]: Applicative]: Otel4sMetrics[F] = Otel4sMetrics(Meter.noop[F], Nil)

    private def decreaseCounter[F[_]](
        req: ServerRequest,
        counter: UpDownCounter[F, Long],
        m: MonadError[F],
        ep: AnyEndpoint
    ) =
        m.suspend(counter.dec(asOpenTelemetryAttributes(ep, req)*))

    private def increaseCounter[F[_]](
        req: ServerRequest,
        counter: UpDownCounter[F, Long],
        m: MonadError[F],
        ep: AnyEndpoint
    ) =
        m.suspend(counter.inc(asOpenTelemetryAttributes(ep, req)*))

    private def requestActive[F[_]: Applicative](
        meter: Meter[F]
    ): F[Metric[F, UpDownCounter[F, Long]]] =
        meter
            .upDownCounter[Long]("http.server.active_requests")
            .withDescription("Active HTTP requests")
            .withUnit("{requests}")
            .create
            .map: counter =>
                Metric[F, UpDownCounter[F, Long]](
                  counter,
                  onRequest = (req, counter, m) =>
                      m.unit:
                          EndpointMetric()
                              .onEndpointRequest: ep =>
                                  increaseCounter(req, counter, m, ep)
                              .onResponseBody: (ep, _) =>
                                  decreaseCounter(req, counter, m, ep)
                              .onException: (ep, _) =>
                                  decreaseCounter(req, counter, m, ep)
                )

    private def requestTotal[F[_]: Applicative](meter: Meter[F]): F[Metric[F, Counter[F, Long]]] =
        meter
            .counter[Long]("http.server.request.total")
            .withDescription("Total HTTP requests")
            .withUnit("1")
            .create
            .map: counter =>
                Metric[F, Counter[F, Long]](
                  counter,
                  onRequest = (req, counter, m) =>
                      m.unit:
                          EndpointMetric()
                              .onResponseBody: (ep, res) =>
                                  m.suspend:
                                      val otLabels =
                                          asOpenTelemetryAttributes(ep, req) ++ asOpenTelemetryAttributes(
                                            Right(res),
                                            None
                                          )
                                      counter.inc(otLabels*)
                              .onException: (ep, ex) =>
                                  m.suspend:
                                      val otLabels =
                                          asOpenTelemetryAttributes(ep, req) ++ asOpenTelemetryAttributes(
                                            Left(ex),
                                            None
                                          )
                                      counter.inc(otLabels*)
                )

    private def requestDuration[F[_]: Applicative](meter: Meter[F]): F[Metric[F, Histogram[F, Long]]] =
        meter
            .histogram[Long]("http.server.request.duration")
            .withDescription("Duration of HTTP requests")
            .withUnit("ms")
            .withExplicitBucketBoundaries(
              BucketBoundaries(Vector(5L, 10L, 25L, 50L, 75L, 100L, 250L, 500L, 750L, 1000L, 2500L, 5000L, 7500L,
                10000L))
            )
            .create
            .map: histogram =>
                Metric[F, Histogram[F, Long]](
                  histogram,
                  onRequest = (req, recorder, m) =>
                      m.eval:
                          val requestStart = Instant.now()

                          def duration = Duration.between(requestStart, Instant.now()).toMillis

                          EndpointMetric()
                              .onResponseHeaders: (ep, res) =>
                                  m.suspend:
                                      val otLabels =
                                          asOpenTelemetryAttributes(ep, req) ++ asOpenTelemetryAttributes(
                                            Right(res),
                                            Some(labels.forResponsePhase.headersValue)
                                          )
                                      recorder.record(duration, otLabels*)
                              .onResponseBody: (ep, res) =>
                                  m.suspend:
                                      val otLabels =
                                          asOpenTelemetryAttributes(ep, req) ++ asOpenTelemetryAttributes(
                                            Right(res),
                                            Some(labels.forResponsePhase.bodyValue)
                                          )
                                      recorder.record(duration, otLabels*)
                              .onException: (ep, ex) =>
                                  m.suspend:
                                      val otLabels =
                                          asOpenTelemetryAttributes(ep, req) ++ asOpenTelemetryAttributes(
                                            Left(ex),
                                            None
                                          )
                                      recorder.record(duration, otLabels*)
                )

    private def requestBodySize[F[_]: Applicative](meter: Meter[F]): F[Metric[F, Histogram[F, Long]]] =
        meter
            .histogram[Long]("http.server.request.body.size")
            .withDescription(
              "The size of the request payload body in bytes. This is the number of bytes transferred excluding headers and is often, but not always, present as the Content-Length header."
            )
            .withUnit("By")
            .create
            .map: histogram =>
                Metric[F, Histogram[F, Long]](
                  histogram,
                  onRequest = (req, recorder, m) =>
                      m.eval:
                          EndpointMetric()
                              .onEndpointRequest: ep =>
                                  m.suspend:
                                      val otLabels = asOpenTelemetryAttributes(ep, req)
                                      recorder.record(req.contentLength.getOrElse(0L), otLabels*)
                )

    private def responseBodySize[F[_]: Applicative](meter: Meter[F]): F[Metric[F, Histogram[F, Long]]] =
        meter
            .histogram[Long]("http.server.response.body.size")
            .withDescription(
              "The size of the response payload body in bytes. This is the number of bytes transferred excluding headers."
            )
            .withUnit("By")
            .create
            .map: histogram =>
                Metric[F, Histogram[F, Long]](
                  histogram,
                  onRequest = (req, recorder, m) =>
                      m.eval:
                          EndpointMetric().onResponseBody: (endpoint, response) =>
                              m.eval:
                                  val otLabels = asOpenTelemetryAttributes(endpoint, req)
                                  response.body.foreach:
                                      case Right((_, Some(length: Long))) => recorder.record(length, otLabels*)
                                      case _                              => m.unit(())
                )

    private def asOpenTelemetryAttributes(ep: AnyEndpoint, req: ServerRequest) =
        labels.forRequest
            .foldLeft(List.empty[Attribute[String]]): (b, label) =>
                b :+ Attribute(AttributeKey.string(label._1), label._2(ep, req))

    private def asOpenTelemetryAttributes(res: Either[Throwable, ServerResponse[?]], phase: Option[String]) =
        val attributes = labels.forResponse
            .foldLeft(List.empty[Attribute[String]]): (b, label) =>
                b :+ Attribute(AttributeKey.string(label._1), label._2(res))
        phase match
            case Some(value) => attributes :+ Attribute(AttributeKey.string(labels.forResponsePhase.name), value)
            case None        => attributes
    end asOpenTelemetryAttributes

end Otel4sMetrics
