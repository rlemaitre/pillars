package pillars.config

import com.comcast.ip4s.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.derivation.ConfiguredDecoder
import io.circe.derivation.ConfiguredEncoder
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
) derives ConfiguredEncoder,
      ConfiguredDecoder
