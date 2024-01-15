package pillars.config

import com.comcast.ip4s.*
import io.circe.Codec
import io.circe.derivation.Configuration
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import pillars.model.PoolSize

final case class HttpServerConfig(
    host: Host = host"0.0.0.0",
    port: Port = port"9876",
    maxConnections: PoolSize = PoolSize(1024),
    enableLogging: Boolean = false
)
object HttpServerConfig:
    given Configuration           = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
    given Codec[HttpServerConfig] = Codec.AsObject.derivedConfigured
