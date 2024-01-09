package pillars.config

import cats.syntax.all.*
import pillars.model.*

case class PillarsConfig[T](
    name: AppName,
    log: LogConfig = LogConfig(),
    db: DatabaseConfig,
    api: ApiConfig,
    admin: AdminConfig,
    observability: ObservabilityConfig = ObservabilityConfig(),
    featureFlags: FeatureFlagsConfig = FeatureFlagsConfig(),
    app: T
)
