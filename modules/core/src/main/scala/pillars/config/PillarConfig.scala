package pillars.config

import cats.syntax.all.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.derivation.ConfiguredDecoder
import io.circe.derivation.ConfiguredEncoder
import io.github.iltotore.iron.circe.given
import pillars.model.*

case class PillarConfig[T](
    name: AppName,
    log: LogConfig = LogConfig(),
    db: DatabaseConfig,
    api: ApiConfig = ApiConfig(),
    admin: AdminConfig = AdminConfig(),
    observability: ObservabilityConfig = ObservabilityConfig(),
    app: T
) derives ConfiguredEncoder,
      ConfiguredDecoder
