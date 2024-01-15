package pillars.db

import cats.effect.*
import cats.effect.std.Console
import cats.syntax.all.*
import fs2.io.net.Network
import org.typelevel.otel4s.trace.Tracer
import pillars.config.DatabaseConfig
import skunk.*

object DB:
    def init[F[_]: Temporal: Network: Console: Tracer](config: DatabaseConfig): SessionPool[F] =
        Session.pooled[F](
          host = config.host.toString,
          port = config.port.value,
          database = config.database,
          user = config.username,
          password = config.password.some.map(_.value),
          max = config.poolSize,
          debug = config.debug
        )
end DB
