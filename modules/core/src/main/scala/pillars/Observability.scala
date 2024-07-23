package pillars

import cats.Parallel
import cats.effect.Async
import cats.effect.LiftIO
import cats.effect.Resource
import cats.effect.std.Console
import cats.effect.syntax.all.*
import cats.syntax.all.*
import io.circe.Codec
import io.circe.derivation.Configuration
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import org.typelevel.otel4s.Attribute
import org.typelevel.otel4s.AttributeKey
import org.typelevel.otel4s.Attributes
import org.typelevel.otel4s.metrics.Meter
import org.typelevel.otel4s.sdk.OpenTelemetrySdk
import org.typelevel.otel4s.sdk.TelemetryResource
import org.typelevel.otel4s.sdk.exporter.otlp.autoconfigure.OtlpExportersAutoConfigure
import org.typelevel.otel4s.trace.Tracer
import sttp.tapir.server.interceptor.EndpointInterceptor
import sttp.tapir.server.interceptor.Interceptor

final case class Observability[F[_]](tracer: Tracer[F], metrics: Meter[F], interceptor: Interceptor[F]):
    export metrics.*
    export tracer.span
    export tracer.spanBuilder
end Observability
object Observability:
    def apply[F[_]: Pillars]: Run[F, Observability[F]] = summon[Pillars[F]].observability
    def noop[F[_]: LiftIO: Async]: F[Observability[F]] =
        Observability(Tracer.noop[F], Meter.noop[F], EndpointInterceptor.noop[F]).pure[F]

    def init[F[_]: LiftIO: Async: Parallel: Console](appInfo: AppInfo, config: Config): Resource[F, Observability[F]] =
        if config.enabled then
            for
                otel4s       <- OpenTelemetrySdk.autoConfigured[F]: builder =>
                                    builder
                                        .addExportersConfigurer(OtlpExportersAutoConfigure[F])
                                        .addResourceCustomizer: (resource, otelConfig) =>
                                            val configured =
                                                TelemetryResource(
                                                  Attributes(
                                                    appInfo.name.toAttribute("service.name"),
                                                    appInfo.version.toAttribute("service.version")
                                                  )
                                                )
                                            resource.mergeUnsafe(configured)
                sdk           = otel4s.sdk
                tracer       <- sdk.tracerProvider.get(config.traces.name.getOrElse(config.serviceName)).toResource
                meter        <- sdk.meterProvider.get(config.metrics.name.getOrElse(config.serviceName)).toResource
                tapirMetrics <- Metrics.init[F](meter).toResource
            yield Observability(tracer, meter, tapirMetrics.metricsInterceptor())
        else
            noop.toResource
    final case class Config(
        enabled: Boolean = false,
        metrics: Config.Metrics = Config.Metrics(),
        traces: Config.Traces = Config.Traces(),
        serviceName: ServiceName = ServiceName("pillars")
    )

    object Config:
        given Configuration = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
        given Codec[Config] = Codec.AsObject.derivedConfigured

        final case class Metrics(
            enabled: Boolean = false,
            name: Option[ServiceName] = None
        ) derives Codec.AsObject
        final case class Traces(
            enabled: Boolean = false,
            name: Option[ServiceName] = None
        ) derives Codec.AsObject
    end Config

    private type ServiceNameConstraint = Not[Blank]
    opaque type ServiceName <: String  = String :| ServiceNameConstraint
    object ServiceName extends RefinedTypeOps[String, ServiceNameConstraint, ServiceName]

    extension [A <: String](value: A)
        def toAttribute(name: String): Attribute[String] = Attribute(name, value)

    extension [A <: Long](value: Long)
        def toAttribute(name: String): Attribute[Long] = Attribute(name, value)

    extension [A <: Double](value: Double)
        def toAttribute(name: String): Attribute[Double] = Attribute(name, value)

    extension [A <: Boolean](value: Boolean)
        def toAttribute(name: String): Attribute[Boolean] = Attribute(name, value)
end Observability
