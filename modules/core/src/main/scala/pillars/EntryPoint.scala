package pillars

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import com.monovore.decline.Command

class EntryPoint(app: App[IO]) extends IOApp:

  override final def run(args: List[String]): IO[ExitCode] =
    Command(app.name, app.description)(CommandOptions.config).parse(args, sys.env) match
      case Left(help) => IO(System.err.println(help)).as(ExitCode.Error)
      case Right(configPath) =>
        Pillars(configPath).use: pillars =>
          app.run(pillars).as(ExitCode.Success)
