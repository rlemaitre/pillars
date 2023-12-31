package pillars.observability

import cats.effect.LiftIO
import cats.effect.kernel.Async
import cats.syntax.all.*
import org.typelevel.otel4s.java.OtelJava
import org.typelevel.otel4s.metrics.Meter
import org.typelevel.otel4s.trace.Tracer
import pillars.config.ObservabilityConfig

final case class Observability[F[_]](tracer: Tracer[F], metrics: Meter[F])

object Observability:

  def noop[F[_]: LiftIO: Async]: F[Observability[F]] = Observability(Tracer.noop[F], Meter.noop[F]).pure[F]
  def init[F[_]: LiftIO: Async](config: ObservabilityConfig): F[Observability[F]] =
    for
      otel4s  <- OtelJava.global
      tracer  <- otel4s.tracerProvider.get(config.serviceName)
      metrics <- otel4s.meterProvider.get(config.serviceName)
    yield Observability(tracer, metrics)
