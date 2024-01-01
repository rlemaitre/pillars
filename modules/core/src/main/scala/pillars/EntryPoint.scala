package pillars

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import org.typelevel.otel4s.trace.Tracer
import pillars.config.ConfigReader
import pillars.config.PillarConfig
import pillars.db.DB
import pillars.observability.Observability

class EntryPoint(app: App[IO]) extends IOApp:

  override def run(args: List[String]): IO[ExitCode] =
    val prog = for
      customConfig <- ConfigReader.readConfig[IO, app.ConfigType]().toResource
      config       <- ConfigReader.readConfig[IO, PillarConfig]().toResource
      obs          <- Observability.init[IO](config.observability).toResource
      pool <- {
        given Tracer[IO] = obs.tracer
        DB.init[IO](config.db)
      }
    yield Pillars(obs, config, pool)
    prog.use: pillars =>
      app.run(pillars).as(ExitCode.Success)
