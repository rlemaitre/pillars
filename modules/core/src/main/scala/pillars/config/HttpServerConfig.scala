package pillars.config

import com.comcast.ip4s.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import org.http4s.BasicCredentials
import pillars.model.PoolSize

final case class HttpServerConfig(
    host: Host = host"localhost",
    port: Port = port"9876",
    authToken: Option[BasicCredentials] = None,
    maxConnections: PoolSize = PoolSize(1024)
)
