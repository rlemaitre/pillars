package pillars

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import com.monovore.decline.Command
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import java.nio.file.Path
import pillars.App.Description
import pillars.App.Name
import pillars.App.Version
import pillars.probes.Probe

trait App[F[_]]:
    def name: Name
    def version: Version
    def description: Description
    def probes: List[Probe[F]] = Nil
    def run(pillars: Pillars[F]): F[Unit]
end App

object App:
    private type NameConstraint = Not[Blank]
    opaque type Name <: String  = String :| NameConstraint

    object Name extends RefinedTypeOps[String, NameConstraint, Name]

    private type VersionConstraint = SemanticVersion
    opaque type Version <: String  = String :| VersionConstraint

    object Version extends RefinedTypeOps[String, VersionConstraint, Version]

    private type DescriptionConstraint = Not[Blank]
    opaque type Description <: String  = String :| DescriptionConstraint

    object Description extends RefinedTypeOps[String, DescriptionConstraint, Description]
end App

trait EntryPoint extends IOApp:

    def app: App[IO]
    override final def run(args: List[String]): IO[ExitCode] =
        Command(app.name, app.description)(Opts.option[Path]("config", "Path to the configuration file")).parse(
          args,
          sys.env
        ) match
        case Left(help)        => IO(System.err.println(help)).as(ExitCode.Error)
        case Right(configPath) =>
            Pillars(configPath).use: pillars =>
                app.run(pillars).as(ExitCode.Success)
end EntryPoint
