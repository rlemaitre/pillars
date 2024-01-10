package pillars.config

import cats.syntax.all.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.derivation.Configuration
import io.github.iltotore.iron.circe.given
import pillars.model.*

case class PillarsConfig(
    name: AppName,
    log: LogConfig = LogConfig(),
    db: DatabaseConfig,
    api: ApiConfig,
    admin: AdminConfig,
    observability: ObservabilityConfig = ObservabilityConfig(),
    featureFlags: FeatureFlagsConfig = FeatureFlagsConfig(),
    healthChecks: ProbesConfig = ProbesConfig()
)

object PillarsConfig:
  given Configuration = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
  given Decoder[PillarsConfig] = Decoder.derivedConfigured
  given Encoder[PillarsConfig] = Encoder.AsObject.derivedConfigured
