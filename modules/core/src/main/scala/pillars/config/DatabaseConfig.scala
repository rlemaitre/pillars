package pillars.config

import com.comcast.ip4s.*
import io.circe.Codec
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
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

object DatabaseConfig:
  given Codec[DatabaseConfig] = Codec.AsObject.derivedConfigured
