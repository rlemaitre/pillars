package pillars.db

import com.comcast.ip4s.*
import io.circe.Codec
import io.circe.derivation.Configuration
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import pillars.config.{ProbeConfig, Secret, given}
import pillars.model.*

final case class DatabaseConfig(
    host: Host = host"localhost",
    port: Port = port"5432",
    database: DatabaseName,
    username: DatabaseUser,
    password: Secret[DatabasePassword],
    poolSize: PoolSize = PoolSize(32),
    debug: Boolean,
    probe: ProbeConfig
)

object DatabaseConfig:
    given Configuration         = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
    given Codec[DatabaseConfig] = Codec.AsObject.derivedConfigured
