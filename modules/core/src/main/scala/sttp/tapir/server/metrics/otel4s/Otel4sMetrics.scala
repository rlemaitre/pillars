package sttp.tapir.server.metrics.otel4s

import cats.Applicative
import cats.Monad
import cats.syntax.all.*
import java.time.Duration
import java.time.Instant
import org.typelevel.otel4s.Attribute
import org.typelevel.otel4s.AttributeKey
import org.typelevel.otel4s.metrics.Counter
import org.typelevel.otel4s.metrics.Histogram
import org.typelevel.otel4s.metrics.Meter
import org.typelevel.otel4s.metrics.UpDownCounter
import sttp.tapir.AnyEndpoint
import sttp.tapir.model.ServerRequest
import sttp.tapir.server.interceptor.metrics.MetricsRequestInterceptor
import sttp.tapir.server.metrics.EndpointMetric
import sttp.tapir.server.metrics.Metric
import sttp.tapir.server.metrics.MetricLabels
import sttp.tapir.server.metrics.otel4s.Otel4sMetrics.*
import sttp.tapir.server.model.ServerResponse

// TODO: copy pasted from atlas, should be contributed to tapir as `tapir-otel4s-metrics`
case class Otel4sMetrics[F[_]: Applicative](meter: Meter[F], metrics: List[Metric[F, _]]):

    /** Registers a `request_active{path, method}` up-down-counter (assuming default labels). */
    def addRequestsActive(labels: MetricLabels = MetricLabels.Default): F[Otel4sMetrics[F]] =
        requestActive(meter, labels).map(m => copy(metrics = metrics :+ m))

    /** Registers a `request_total{path, method, status}` counter (assuming default labels). */
    def addRequestsTotal(labels: MetricLabels = MetricLabels.Default): F[Otel4sMetrics[F]] =
        requestTotal(meter, labels).map(m => copy(metrics = metrics :+ m))

    /** Registers a `request_duration_seconds{path, method, status, phase}` histogram (assuming default labels). */
    def addRequestsDuration(labels: MetricLabels = MetricLabels.Default): F[Otel4sMetrics[F]] =
        requestDuration(meter, labels).map(m => copy(metrics = metrics :+ m))

    /** Registers a custom metric. */
    def addCustom(m: Metric[F, ?]): Otel4sMetrics[F] = copy(metrics = metrics :+ m)

    /** The interceptor which can be added to a server's options, to enable metrics collection. */
    def metricsInterceptor(ignoreEndpoints: Seq[AnyEndpoint] = Seq.empty): MetricsRequestInterceptor[F] =
        new MetricsRequestInterceptor[F](metrics, ignoreEndpoints)
end Otel4sMetrics

object Otel4sMetrics:

    /** Using the default labels, registers the following metrics:
    *
    *   - `request_active{path, method}` (up-down-counter)
    *   - `request_total{path, method, status}` (counter)
    *   - `request_duration{path, method, status, phase}` (histogram)
    *
    * Status is by default the status code class (1xx, 2xx, etc.), and phase can be either `headers` or `body` - request duration is
    * measured separately up to the point where the headers are determined, and then once again when the whole response body is complete.
    */
    def init[F[_]: Monad](meter: Meter[F], labels: MetricLabels = MetricLabels.Default): F[Otel4sMetrics[F]] =
        for
            active   <- requestActive(meter, labels)
            total    <- requestTotal(meter, labels)
            duration <- requestDuration(meter, labels)
        yield Otel4sMetrics(meter, List[Metric[F, _]](active, total, duration))
    def init[F[_]: Applicative](meter: Meter[F], metrics: List[Metric[F, _]]): F[Otel4sMetrics[F]]           =
        Otel4sMetrics(meter, metrics).pure[F]

    def noop[F[_]: Applicative]: Otel4sMetrics[F] = Otel4sMetrics(Meter.noop[F], Nil)

    private def requestActive[F[_]: Applicative](
        meter: Meter[F],
        labels: MetricLabels
    ): F[Metric[F, UpDownCounter[F, Long]]] =
        meter
            .upDownCounter("request.active")
            .withDescription("Active HTTP requests")
            .withUnit("1")
            .create
            .map: counter =>
                Metric[F, UpDownCounter[F, Long]](
                  counter,
                  onRequest = (req, counter, m) =>
                      m.unit {
                          EndpointMetric()
                              .onEndpointRequest { ep =>
                                  m.suspend(counter.inc(asOpenTelemetryAttributes(labels, ep, req)*))
                              }
                              .onResponseBody { (ep, _) =>
                                  m.suspend(counter.dec(asOpenTelemetryAttributes(labels, ep, req)*))
                              }
                              .onException { (ep, _) =>
                                  m.suspend(counter.dec(asOpenTelemetryAttributes(labels, ep, req)*))
                              }
                      }
                )

    private def requestTotal[F[_]: Applicative](meter: Meter[F], labels: MetricLabels): F[Metric[F, Counter[F, Long]]] =
        meter
            .counter("request.total")
            .withDescription("Total HTTP requests")
            .withUnit("1")
            .create
            .map: counter =>
                Metric[F, Counter[F, Long]](
                  counter,
                  onRequest = (req, counter, m) =>
                      m.unit {
                          EndpointMetric()
                              .onResponseBody { (ep, res) =>
                                  m.suspend {
                                      val otLabels =
                                          merge(
                                            asOpenTelemetryAttributes(labels, ep, req),
                                            asOpenTelemetryAttributes(labels, Right(res), None)
                                          )
                                      counter.inc(otLabels*)
                                  }
                              }
                              .onException { (ep, ex) =>
                                  m.suspend {
                                      val otLabels =
                                          merge(
                                            asOpenTelemetryAttributes(labels, ep, req),
                                            asOpenTelemetryAttributes(labels, Left(ex), None)
                                          )
                                      counter.inc(otLabels*)
                                  }
                              }
                      }
                )

    private def requestDuration[F[_]: Applicative](
        meter: Meter[F],
        labels: MetricLabels
    ): F[Metric[F, Histogram[F, Double]]] =
        meter
            .histogram("request.duration")
            .withDescription("Duration of HTTP requests")
            .withUnit("ms")
            .create
            .map: histogram =>
                Metric[F, Histogram[F, Double]](
                  histogram,
                  onRequest = (req, recorder, m) =>
                      m.eval {
                          val requestStart = Instant.now()

                          def duration = Duration.between(requestStart, Instant.now()).toMillis.toDouble

                          EndpointMetric()
                              .onResponseHeaders { (ep, res) =>
                                  m.suspend {
                                      val otLabels =
                                          merge(
                                            asOpenTelemetryAttributes(labels, ep, req),
                                            asOpenTelemetryAttributes(
                                              labels,
                                              Right(res),
                                              Some(labels.forResponsePhase.headersValue)
                                            )
                                          )
                                      recorder.record(duration, otLabels*)
                                  }
                              }
                              .onResponseBody { (ep, res) =>
                                  m.suspend {
                                      val otLabels =
                                          merge(
                                            asOpenTelemetryAttributes(labels, ep, req),
                                            asOpenTelemetryAttributes(
                                              labels,
                                              Right(res),
                                              Some(labels.forResponsePhase.bodyValue)
                                            )
                                          )
                                      recorder.record(duration, otLabels*)
                                  }
                              }
                              .onException { (ep, ex) =>
                                  m.suspend {
                                      val otLabels =
                                          merge(
                                            asOpenTelemetryAttributes(labels, ep, req),
                                            asOpenTelemetryAttributes(labels, Left(ex), None)
                                          )
                                      recorder.record(duration, otLabels*)
                                  }
                              }
                      }
                )

    def asOpenTelemetryAttributes(l: MetricLabels, ep: AnyEndpoint, req: ServerRequest): List[Attribute[String]] =
        l.forRequest
            .foldLeft(List.empty[Attribute[String]]) { (b, label) =>
                b :+ Attribute(AttributeKey.string(label._1), label._2(ep, req))
            }

    def asOpenTelemetryAttributes(
        l: MetricLabels,
        res: Either[Throwable, ServerResponse[_]],
        phase: Option[String]
    ): List[Attribute[String]] =
        val attributes = l.forResponse
            .foldLeft(List.empty[Attribute[String]]) { (b, label) =>
                b :+ Attribute(AttributeKey.string(label._1), label._2(res))
            }
        phase match
        case Some(value) => attributes :+ Attribute(AttributeKey.string(l.forResponsePhase.name), value)
        case None        => attributes
    end asOpenTelemetryAttributes

    private def merge(a1: List[Attribute[String]], a2: List[Attribute[String]]): List[Attribute[String]] = a1 ++ a2
end Otel4sMetrics
