package pillars.httpclient

import cats.effect.Async
import cats.syntax.all.*
import org.http4s.Method
import org.http4s.Status
import org.http4s.metrics.MetricsOps
import org.http4s.metrics.TerminationType
import org.typelevel.otel4s.metrics.Counter
import org.typelevel.otel4s.metrics.Histogram
import org.typelevel.otel4s.metrics.Meter
import org.typelevel.otel4s.metrics.UpDownCounter
import pillars.Observability

final case class MetricsCollection[F[_]](
    responseDuration: Histogram[F, Long],
    activeRequests: UpDownCounter[F, Long],
    requests: Counter[F, Long],
    abnormalTerminations: Histogram[F, Long]
)

object MetricsCollection:
    def create[F[_]: Async](meter: Meter[F]): F[MetricsCollection[F]] =
        (
          meter.histogram[Long]("http.client.request.duration")
              .withUnit("s")
              .withDescription("Response time in seconds")
              .create,
          meter
              .upDownCounter[Long]("http.client.open_connections")
              .withUnit("{connection}")
              .withDescription("Active requests")
              .create,
          meter.counter[Long]("http.client.requests.active.total")
              .withUnit("1")
              .withDescription("Total requests")
              .create,
          meter.histogram[Long]("http.client.requests.abnormal_terminations_milliseconds")
              .withUnit("ms")
              .withDescription("Abnormal terminations time in milliseconds")
              .create
        ).mapN(MetricsCollection.apply)
end MetricsCollection

final case class ClientMetrics[F[_]: Async](metrics: MetricsCollection[F]) extends MetricsOps[F]:
    import pillars.Observability.*
    override def increaseActiveRequests(classifier: Option[String]): F[Unit] =
        metrics.activeRequests.inc(label(classifier).toAttribute("classifier"))

    override def decreaseActiveRequests(classifier: Option[String]): F[Unit] =
        metrics.activeRequests.dec(label(classifier).toAttribute("classifier"))

    override def recordHeadersTime(
        method: Method,
        elapsed: Long,
        classifier: Option[String]
    ): F[Unit] =
        metrics.responseDuration
            .record(
              elapsed,
              label(classifier).toAttribute("classifier"),
              reportMethod(method).toAttribute("method"),
              Phase.Headers.report.toAttribute("phase")
            )

    override def recordTotalTime(
        method: Method,
        status: Status,
        elapsed: Long,
        classifier: Option[String]
    ): F[Unit] =
        for
            _ <- metrics.responseDuration
                     .record(
                       elapsed,
                       label(classifier).toAttribute("classifier"),
                       reportMethod(method).toAttribute("method"),
                       Phase.Body.report.toAttribute("phase")
                     )
            _ <- metrics.requests
                     .inc(
                       label(classifier).toAttribute("classifier"),
                       reportMethod(method).toAttribute("method"),
                       reportStatus(status).toAttribute("status")
                     )
        yield ()

    override def recordAbnormalTermination(
        elapsed: Long,
        terminationType: TerminationType,
        classifier: Option[String]
    ): F[Unit] =
        terminationType match
        case TerminationType.Abnormal(e) => recordAbnormal(elapsed, classifier, e)
        case TerminationType.Error(e)    => recordError(elapsed, classifier, e)
        case TerminationType.Canceled    => recordCanceled(elapsed, classifier)
        case TerminationType.Timeout     => recordTimeout(elapsed, classifier)

    private def recordCanceled(elapsed: Long, classifier: Option[String]): F[Unit] =
        metrics.abnormalTerminations
            .record(
              elapsed,
              label(classifier).toAttribute("classifier"),
              AbnormalTermination.Canceled.report.toAttribute("type")
            )

    private def recordAbnormal(
        elapsed: Long,
        classifier: Option[String],
        cause: Throwable
    ): F[Unit] =
        metrics.abnormalTerminations
            .record(
              elapsed,
              label(classifier).toAttribute("classifier"),
              AbnormalTermination.Abnormal.report.toAttribute("type"),
              cause.getClass.getName.toAttribute("cause")
            )

    private def recordError(
        elapsed: Long,
        classifier: Option[String],
        cause: Throwable
    ): F[Unit] =
        metrics.abnormalTerminations
            .record(
              elapsed,
              label(classifier).toAttribute("classifier"),
              AbnormalTermination.Error.report.toAttribute("type"),
              cause.getClass.getName.toAttribute("cause")
            )

    private def recordTimeout(elapsed: Long, classifier: Option[String]): F[Unit] =
        metrics.abnormalTerminations
            .record(
              elapsed,
              label(classifier).toAttribute("classifier"),
              AbnormalTermination.Timeout.report.toAttribute("type")
            )

    private def label(value: Option[String]): String = value.getOrElse("")

    private def reportStatus(status: Status): String =
        status.code match
        case informational if informational < 200 => "1xx"
        case success if success < 300             => "2xx"
        case redirect if redirect < 400           => "3xx"
        case clientError if clientError < 500     => "4xx"
        case _                                    => "5xx"

    private def reportMethod(m: Method): String =
        m match
        case Method.GET     => "get"
        case Method.PUT     => "put"
        case Method.POST    => "post"
        case Method.PATCH   => "patch"
        case Method.HEAD    => "head"
        case Method.MOVE    => "move"
        case Method.OPTIONS => "options"
        case Method.TRACE   => "trace"
        case Method.CONNECT => "connect"
        case Method.DELETE  => "delete"
        case _              => "other"
end ClientMetrics

object ClientMetrics:
    def apply[F[_]: Async](observability: Observability[F]): F[ClientMetrics[F]] =
        MetricsCollection.create(observability.metrics).map(ClientMetrics.apply)

enum Phase:
    case Headers, Body

    def report: String =
        this match
        case Headers => "headers"
        case Body    => "body"
end Phase

enum AbnormalTermination:
    case Abnormal, Error, Timeout, Canceled

    def report: String =
        this match
        case Abnormal => "abnormal"
        case Timeout  => "timeout"
        case Error    => "error"
        case Canceled => "cancel"
end AbnormalTermination
