package pillars.cli

import cats.effect.ExitCode
import cats.effect.IO
import com.monovore.decline.*
import com.monovore.decline.effect.*
import pillars.cli.build.BuildInfo

object Main extends CommandIOApp(
      name = "Pillars",
      header = "Pillars CLI tool",
      helpFlag = true,
      version = BuildInfo.version
    ):
    override def main: Opts[IO[ExitCode]] =
        Opts.subcommand(New.command).map:
            case New() => IO.pure(ExitCode.Success)

end Main
