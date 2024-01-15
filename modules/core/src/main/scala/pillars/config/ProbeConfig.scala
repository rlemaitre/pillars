package pillars.config

import io.circe.*
import io.circe.syntax.*
import io.github.iltotore.iron.circe.given
import org.http4s.Uri
import pillars.probes.Component
import scala.concurrent.duration.*

case class ProbesConfig(enabled: Boolean = true, probes: List[ProbeConfig] = List.empty)
object ProbesConfig:
    given Codec[ProbesConfig] = Codec.AsObject.derivedConfigured

enum ProbeConfig(
    val name: Component.Name,
    val timeout: FiniteDuration,
    val interval: FiniteDuration,
    val failureCount: Int
):
    case Database(
        override val name: Component.Name,
        override val timeout: FiniteDuration = 2.seconds,
        override val interval: FiniteDuration = 30.seconds,
        override val failureCount: Int = 3
    ) extends ProbeConfig(name, timeout, interval, failureCount)
    case Http(
        override val name: Component.Name,
        override val timeout: FiniteDuration = 2.seconds,
        override val interval: FiniteDuration = 30.seconds,
        override val failureCount: Int = 3,
        url: Uri
    ) extends ProbeConfig(name, timeout, interval, failureCount)

    def component: Component = this match
    case database: Database => Component(name, Component.Type.Datastore)
    case http: Http         => Component(name, Component.Type.Component)
end ProbeConfig

object ProbeConfig:
    given dbEncoder: Encoder[ProbeConfig.Database] =
        Encoder.instance: db =>
            Json.obj(
              "type"          := "database",
              "name"          := db.name,
              "timeout"       := db.timeout,
              "interval"      := db.interval,
              "failure-count" := db.failureCount
            )

    given dbDecoder: Decoder[ProbeConfig.Database] =
        Decoder.forProduct5("type", "name", "timeout", "interval", "failure-count")(
          (`type`: String, name: Component.Name, timeout: FiniteDuration, interval: FiniteDuration, failures: Int) =>
              ProbeConfig.Database(name, timeout, interval, failures)
        )

    given httpEncoder: Encoder[ProbeConfig.Http] =
        Encoder.instance: http =>
            Json.obj(
              "type"          := "http",
              "name"          := http.name,
              "timeout"       := http.timeout,
              "interval"      := http.interval,
              "failure-count" := http.failureCount,
              "url"           := http.url
            )

    given httpDecoder: Decoder[ProbeConfig.Http] =
        Decoder.forProduct6("type", "name", "timeout", "interval", "failure-count", "url")(
          (
              `type`: String,
              name: Component.Name,
              timeout: FiniteDuration,
              interval: FiniteDuration,
              failures: Int,
              url: Uri
          ) => ProbeConfig.Http(name, timeout, interval, failures, url)
        )

    given Encoder[ProbeConfig] = Encoder.instance:
        case database: ProbeConfig.Database => dbEncoder(database)
        case http: ProbeConfig.Http         => httpEncoder(http)

    given Decoder[ProbeConfig] = Decoder.instance: cursor =>
        cursor
            .downField("type")
            .as[String]
            .flatMap:
                case "database" => dbDecoder(cursor)
                case "http"     => httpDecoder(cursor)
                case other      => Left(DecodingFailure(s"Unknown probe type: $other", cursor.history))
end ProbeConfig
