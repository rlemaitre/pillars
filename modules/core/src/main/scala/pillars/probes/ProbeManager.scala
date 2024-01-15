package pillars.probes

import cats.Applicative
import cats.effect.Resource
import cats.effect.kernel.Async
import cats.effect.std.MapRef
import cats.syntax.all.*
import fs2.Stream
import org.http4s.client.Client
import pillars.config.ProbeConfig
import pillars.config.ProbesConfig
import skunk.Session
import skunk.codec.all
import skunk.implicits.*

trait ProbeManager[F[_]]:
    def status: F[Map[Component, Status]]
    def globalStatus: F[Status]
    def start(): F[Unit]
end ProbeManager

object ProbeManager:
    private def noop[F[_]: Applicative]: ProbeManager[F] = new ProbeManager[F]:
        def status: F[Map[Component, Status]] = Map.empty.pure[F]
        def globalStatus: F[Status]           = Status.pass.pure[F]
        def start(): F[Unit]                  = ().pure[F]
    def build[F[_]: Async](
        config: ProbesConfig,
        pool: Resource[F, Session[F]],
        client: Client[F]
    ): Resource[F, ProbeManager[F]] =
        if !config.enabled then Resource.eval(noop.pure[F])
        else
            Resource.eval:
                for
                    componentErrors               <- MapRef.ofConcurrentHashMap[F, Component, Int]()
                    streams: List[Stream[F, Unit]] = createStreams(config, pool, client, componentErrors)
                    limits                         = config.probes.map(c => c.component -> c.failureCount).toMap
                yield new ProbeManager[F]:
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

    private def buildCheckStream[F[_]: Async](
        probe: ProbeConfig,
        componentErrors: MapRef[F, Component, Option[Int]]
    )(check: F[Boolean]): Stream[F, Unit] =
        Stream
            .fixedRate(probe.interval)
            .evalMap: _ =>
                check
                    .attemptTap:
                        case Right(value) if value => componentErrors(probe.component).update(_ => 0.some)
                        case _                     =>
                            componentErrors(probe.component).update:
                                case Some(value) => Some(value + 1)
                                case None        => Some(1)
                    .void

    private def createStreams[F[_]: Async](
        config: ProbesConfig,
        pool: Resource[F, Session[F]],
        client: Client[F],
        componentErrors: MapRef[F, Component, Option[Int]]
    ): List[Stream[F, Unit]] =
        config.probes.map:
            case db: ProbeConfig.Database =>
                buildCheckStream(db, componentErrors):
                    pool.use(session => session.unique(sql"select true".query(all.bool)))
            case http: ProbeConfig.Http   =>
                buildCheckStream(http, componentErrors):
                    client.get(http.url)(response => response.body.compile.drain *> response.status.isSuccess.pure[F])
end ProbeManager
