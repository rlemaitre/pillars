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
import pillars.api.ApiServer
import pillars.config.ConfigReader
import pillars.config.PillarConfig
import pillars.db.DB
import pillars.logging.Log
import pillars.observability.Observability
import scribe.Scribe
import scribe.ScribeImpl
import skunk.Session

final case class Pillars[F[_]: Sync, Config](
    observability: Observability[F],
    config: PillarConfig[Config],
    pool: Resource[F, Session[F]],
    apiServer: ApiServer[F]
):
  val logger: Scribe[F] = ScribeImpl(Sync[F])

object Pillars:
  def apply[F[_]: LiftIO: Async: Console: Network, Config: Decoder](configPath: Path): Resource[F, Pillars[F, Config]] =
    for
      config <- ConfigReader.readConfig[F, Config](configPath)
      obs    <- Resource.eval(Observability.init[F](config.observability))
      given Tracer[F] = obs.tracer
      _    <- Resource.eval(Log.init(config.log))
      _    <- Spawn[F].background(AdminServer(config.admin, obs).start())
      pool <- DB.init[F](config.db)
      api = ApiServer.init(config.api, obs)
    yield Pillars(obs, config, pool, api)
