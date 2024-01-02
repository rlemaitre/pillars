package pillars.config

import cats.syntax.all.*
import io.circe.Codec
import io.github.iltotore.iron.circe.given
import pillars.model.*

case class PillarConfig(
    name: AppName,
    db: DatabaseConfig,
    api: ApiConfig,
    admin: AdminConfig,
    observability: ObservabilityConfig
) derives Codec.AsObject
