package pillars

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import fs2.io.net.Network
import org.http4s.client.Client
import org.typelevel.otel4s.trace.Tracer
import pillars.Loader.Context
import pillars.config.ConfigReader
import pillars.observability.Observability
import scribe.Scribe

trait Loader:
    type M[F[_]] <: Module[F]
    def name: String

    def load[F[_]: Async: Network: Tracer: Console](context: Context[F]): Resource[F, M[F]]
end Loader

object Loader:
    final case class Context[F[_]: Async: Network: Tracer: Console](
        observability: Observability[F],
        configReader: ConfigReader[F],
        logger: Scribe[F],
        client: Client[F]
    )
end Loader
