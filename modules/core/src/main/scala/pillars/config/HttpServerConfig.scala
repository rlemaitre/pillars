package pillars.config

import com.comcast.ip4s.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.derivation.ConfiguredDecoder
import io.circe.derivation.ConfiguredEncoder
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import pillars.model.PoolSize

final case class HttpServerConfig(
    host: Host = host"localhost",
    port: Port = port"9876",
    maxConnections: PoolSize = PoolSize(1024)
) derives ConfiguredEncoder,
      ConfiguredDecoder
