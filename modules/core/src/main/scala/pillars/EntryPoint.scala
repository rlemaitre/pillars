package pillars

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.syntax.all.*
import com.monovore.decline.Command
import io.circe.Decoder
import org.typelevel.otel4s.trace.Tracer
import pillars.config.ConfigReader
import pillars.config.PillarConfig
import pillars.db.DB
import pillars.observability.Observability

class EntryPoint(app: App[IO])(using Decoder[app.Config]) extends IOApp:

  override final def run(args: List[String]): IO[ExitCode] =
    Command(app.name, app.description)((CommandOptions.config, CommandOptions.logLevel).tupled)
      .parse(args, sys.env) match
      case Left(help) =>
        IO(System.err.println(help)).as(ExitCode.Error)
      case Right((configPath, logLevel)) =>
        val prog = for
          customConfig <- ConfigReader.readConfig[app.Config](configPath)(using Decoder[app.Config])
          config       <- ConfigReader.readConfig[PillarConfig](configPath)
          obs          <- Observability.init[IO](config.observability).toResource
          pool <- {
            given Tracer[IO] = obs.tracer
            DB.init[IO](config.db)
          }
        yield Pillars(obs, config, pool)
        prog.use: pillars =>
          app.run(pillars).as(ExitCode.Success)
