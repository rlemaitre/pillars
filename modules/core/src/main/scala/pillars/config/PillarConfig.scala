package pillars.config

import pillars.model.*
case class PillarConfig(
    name: AppName,
    db: DatabaseConfig,
    api: ApiConfig,
    admin: AdminConfig,
    observability: ObservabilityConfig
)
