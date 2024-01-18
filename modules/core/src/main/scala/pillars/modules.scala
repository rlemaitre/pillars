package pillars

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import fs2.io.net.Network
import org.typelevel.otel4s.trace.Tracer
import pillars.config.Reader
import pillars.probes.Probe
import scala.reflect.ClassTag
import scribe.Scribe

trait Module[F[_]]:
    def probes: List[Probe[F]] = Nil

    def adminControllers: List[Controller[F]] = Nil
end Module

case class Modules[F[_]](private val values: Map[Class[?], Module[F]]):
    def add[K <: Module[F]](value: K): Modules[F] =
        val clazz = if value.getClass.isAnonymousClass then
            value.getClass.getInterfaces.filterNot(_ == classOf[Module[F]]).head
        else value.getClass
        Modules(values + (clazz -> value))
    end add
    def get[K: ClassTag]: K                       = values(summon[ClassTag[K]].runtimeClass).asInstanceOf[K]
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
