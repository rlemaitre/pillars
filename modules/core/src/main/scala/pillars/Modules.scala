package pillars

import pillars.http.server.Controller
import pillars.probes.Probe
import scala.reflect.ClassTag

case class Modules[F[_]](private val values: Map[Class[?], Module[F]]):
    def add[K <: Module[F]](value: K): Modules[F] =
        val clazz = if value.getClass.isAnonymousClass then value.getClass.getInterfaces.head else value.getClass
        Modules(values + (clazz -> value))
    def get[K: ClassTag]: K                       = values(summon[ClassTag[K]].runtimeClass).asInstanceOf[K]
    export values.values as all
    def probes: List[Probe[F]]                    = all.flatMap(_.probes).toList
    def adminControllers: List[Controller[F]]     = all.flatMap(_.adminControllers).toList
end Modules
object Modules:
    def empty[F[_]]: Modules[F] = Modules(Map.empty)
