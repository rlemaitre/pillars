package pillars.probes

import cats.effect.Resource
import cats.effect.kernel.Async
import cats.effect.std.MapRef
import cats.syntax.all.*
import fs2.Stream
import pillars.Modules

trait ProbeManager[F[_]]:
    def status: F[Map[Component, Status]]
    def globalStatus: F[Status]
    def start(): F[Unit]
end ProbeManager

object ProbeManager:
    def build[F[_]: Async](modules: Modules[F]): Resource[F, ProbeManager[F]] =
        Resource.eval:
            val probes = modules.all.flatMap(_.probes).toList
            val limits = probes.map(c => c.component -> c.config.failureCount).toMap
            MapRef.ofConcurrentHashMap[F, Component, Int]().map: componentErrors =>
                val streams: List[Stream[F, Unit]] =
                    probes.map: probe =>
                        Stream
                            .fixedRate(probe.config.interval)
                            .evalMap: _ =>
                                probe.check
                                    .attemptTap:
                                        case Right(value) if value =>
                                            componentErrors(probe.component).update(_ => 0.some)
                                        case _                     =>
                                            componentErrors(probe.component).update:
                                                case Some(value) => Some(value + 1)
                                                case None        => Some(1)
                                    .void
                new ProbeManager[F]:
                    def start(): F[Unit]                  = Stream(streams*).parJoinUnbounded.compile.drain
                    def status: F[Map[Component, Status]] =
                        limits.toList
                            .traverse: (component, limit) =>
                                componentErrors(component).get.map:
                                    case Some(value) =>
                                        if value == 0 then component -> Status.pass
                                        else if value < limit then component -> Status.warn
                                        else component                       -> Status.fail
                                    case None        => component -> Status.pass
                            .map(_.toMap)
                    def globalStatus: F[Status]           =
                        status.map(_.values.toList.foldLeft(Status.pass)(_ |+| _))
                end new
end ProbeManager
