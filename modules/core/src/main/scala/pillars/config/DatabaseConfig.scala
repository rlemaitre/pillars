package pillars.config

import com.comcast.ip4s.*
import io.github.iltotore.iron.*
import pillars.model.*

final case class DatabaseConfig(
    host: Host = host"localhost",
    port: Port = port"5432",
    database: DatabaseName,
    username: DatabaseUser,
    password: Secret[DatabasePassword],
    poolSize: PoolSize = PoolSize(32),
    debug: Boolean
)
