package pillars.config

import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import io.circe.Codec
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import pillars.model.*

final case class DatabaseConfig(
    host: Host,
    port: Port,
    database: DatabaseName,
    username: DatabaseUser,
    password: Secret[DatabasePassword],
    poolSize: PoolSize,
    debug: Boolean
) derives Codec.AsObject
