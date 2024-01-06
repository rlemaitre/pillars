package pillars.config

import cats.syntax.all.*
import io.circe.Decoder
import io.circe.Encoder
import io.github.iltotore.iron.circe.given
import pillars.model.*

case class PillarConfig[T](
    name: AppName,
    log: LogConfig = LogConfig(),
    db: DatabaseConfig,
    api: ApiConfig,
    admin: AdminConfig,
    observability: ObservabilityConfig = ObservabilityConfig(),
    app: T
)

object PillarConfig:
  given [T: Decoder]: Decoder[PillarConfig[T]] = Decoder.derivedConfigured
  given [T: Encoder]: Encoder[PillarConfig[T]] = Encoder.AsObject.derivedConfigured
