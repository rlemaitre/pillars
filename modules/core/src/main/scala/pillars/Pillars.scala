package pillars

import cats.effect.Async
import cats.effect.LiftIO
import cats.effect.Resource
import cats.effect.Spawn
import cats.effect.Sync
import cats.effect.std.Console
import cats.syntax.all.*
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

final case class Pillars[F[_]: Sync](
    observability: Observability[F],
    config: PillarsConfig,
    pool: Resource[F, Session[F]],
    apiServer: ApiServer[F],
    flags: FlagManager[F],
    private val configPath: Path
):
  val logger: Scribe[F]                      = ScribeImpl(Sync[F])
  def readConfig[T: Decoder]: Resource[F, T] = ConfigReader.readConfig[F, T](configPath)
object Pillars:
  def apply[F[_]: LiftIO: Async: Console: Network](configPath: Path): Resource[F, Pillars[F]] =
    for
      config <- ConfigReader.readConfig[F, PillarsConfig](configPath)
      obs    <- Resource.eval(Observability.init[F](config.observability))
      given Tracer[F] = obs.tracer
      _      <- Resource.eval(Log.init(config.log))
      pool   <- DB.init[F](config.db)
      flags  <- Resource.eval(FlagManager.init[F](config.featureFlags))
      client <- HttpClient.build[F]()
      probes <- ProbeManager.build[F](config.healthChecks, pool, client)
      _      <- Spawn[F].background(probes.start())
      _ <- Spawn[F].background(
        AdminServer[F](config.admin, obs, List(ProbesController(probes), FlagController(flags))).start()
      )
      api = ApiServer.init(config.api, obs)
    yield Pillars(obs, config, pool, api, flags, configPath)
