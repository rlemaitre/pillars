package pillars

import cats.effect.*
import cats.effect.std.Console
import fs2.io.net.Network
import io.circe.Decoder
import java.nio.file.Path
import org.typelevel.otel4s.trace.Tracer
import pillars.admin.AdminServer
import pillars.admin.controllers.FlagController
import pillars.admin.controllers.ProbesController
import pillars.api.ApiServer
import pillars.config.ConfigReader
import pillars.config.PillarsConfig
import pillars.db.DB
import pillars.flags.FlagManager
import pillars.http.client.HttpClient
import pillars.logging.Log
import pillars.observability.Observability
import pillars.probes.ProbeManager
import scribe.Scribe
import scribe.ScribeImpl
import skunk.Session

trait Pillars[F[_]]:
    def observability: Observability[F]
    def config: PillarsConfig
    def pool: Resource[F, Session[F]]
    def apiServer: ApiServer[F]
    def flags: FlagManager[F]
    def logger: Scribe[F]
    def readConfig[T](using Decoder[T]): F[T]
end Pillars

object Pillars:
    def apply[F[_]: LiftIO: Async: Console: Network](path: Path): Resource[F, Pillars[F]] =
        for
            _config        <- Resource.eval(ConfigReader.readConfig[F, PillarsConfig](path))
            obs            <- Resource.eval(Observability.init[F](_config.observability))
            given Tracer[F] = obs.tracer
            _              <- Resource.eval(Log.init(_config.log))
            _pool          <- DB.init[F](_config.db)
            _flags         <- Resource.eval(FlagManager.init[F](_config.featureFlags))
            client         <- HttpClient.build[F]()
            probes         <- ProbeManager.build[F](_config.healthChecks, _pool, client)
            _              <- Spawn[F].background(probes.start())
            _              <- Spawn[F].background(
                                AdminServer[F](_config.admin, obs, List(ProbesController(probes), FlagController(_flags))).start()
                              )
        yield new Pillars[F]:
            override def observability: Observability[F] = obs

            override def config: PillarsConfig = _config

            override def pool: Resource[F, Session[F]] = _pool

            override def apiServer: ApiServer[F] =
                ApiServer.init(config.api, observability, logger)

            override def flags: FlagManager[F] = _flags

            override def logger: Scribe[F] = ScribeImpl[F](Sync[F])

            override def readConfig[T](using Decoder[T]): F[T] =
                ConfigReader.readConfig[F, T](path)
end Pillars
