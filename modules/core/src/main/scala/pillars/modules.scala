package pillars

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import fs2.io.net.Network
import org.typelevel.otel4s.trace.Tracer
import pillars.Config.Reader
import pillars.probes.Probe
import scribe.Scribe

trait Module[F[_]]:
    def probes: List[Probe[F]] = Nil

    def adminControllers: List[Controller[F]] = Nil

    def key: Module.Key
end Module

object Module:
    trait Key

case class Modules[F[_]](private val values: Map[Module.Key, Module[F]]):
    def add[K <: Module[F]](value: K): Modules[F] = Modules(values + (value.key -> value))
    def get[K](key: Module.Key): K                = values(key).asInstanceOf[K]
    export values.size
    export values.values as all
    def probes: List[Probe[F]]                    = all.flatMap(_.probes).toList
    def adminControllers: List[Controller[F]]     = all.flatMap(_.adminControllers).toList
end Modules
object Modules:
    def empty[F[_]]: Modules[F] = Modules(Map.empty)

trait Loader:
    type M[F[_]] <: Module[F]
    def name: String
    def dependsOn: Set[Loader] = Set.empty

    def load[F[_]: Async: Network: Tracer: Console](
        context: Loader.Context[F],
        modules: Modules[F] = Modules.empty
    ): Resource[F, M[F]]
end Loader

object Loader:
    final case class Context[F[_]: Async: Network: Tracer: Console](
        observability: Observability[F],
        configReader: Reader[F],
        logger: Scribe[F]
    )
end Loader
