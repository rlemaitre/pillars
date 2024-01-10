package pillars.json

import io.circe.Codec
import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.Encoder
import io.circe.Json
import io.circe.derivation.Configuration
import io.circe.syntax.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.circe.given
import org.http4s.Uri
import pillars.config.*
import pillars.json.flags.given
import pillars.probes.Component
import scala.concurrent.duration.FiniteDuration

object config:
  given Configuration = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
  given Codec[FeatureFlagsConfig] = Codec.AsObject.derivedConfigured

  given Codec[HttpServerConfig] = Codec.AsObject.derivedConfigured

  given Codec[LogConfig] = Codec.AsObject.derivedConfigured

  given [T: Decoder]: Decoder[PillarsConfig[T]] = Decoder.derivedConfigured

  given [T: Encoder]: Encoder[PillarsConfig[T]] = Encoder.AsObject.derivedConfigured

  given Codec[ObservabilityConfig] = Codec.AsObject.derivedConfigured

  given Codec[AdminConfig] = Codec.AsObject.derivedConfigured

  given Codec[ApiConfig] = Codec.AsObject.derivedConfigured

  given Codec[DatabaseConfig] = Codec.AsObject.derivedConfigured

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
  given Codec[ProbesConfig] = Codec.AsObject.derivedConfigured
