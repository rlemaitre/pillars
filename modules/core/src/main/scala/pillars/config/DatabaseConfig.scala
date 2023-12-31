package pillars.config

import ciris.Secret
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import pillars.model.*

final case class DatabaseConfig(
    host: Host,
    port: Port,
    database: DatabaseName,
    schema: DatabaseSchema,
    username: DatabaseUser,
    password: Secret[DatabasePassword],
    poolSize: PoolSize,
    schemaBaselineVersion: SchemaVersion,
    debug: Boolean,
    migrate: Boolean
)
