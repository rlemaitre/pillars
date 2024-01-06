package pillars.config

import com.comcast.ip4s.*
import io.circe.Codec
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import pillars.model.PoolSize

final case class HttpServerConfig(
    host: Host = host"0.0.0.0",
    port: Port = port"9876",
    maxConnections: PoolSize = PoolSize(1024),
    enableLogging: Boolean = false
)

object HttpServerConfig:
  given Codec[HttpServerConfig] = Codec.AsObject.derivedConfigured
