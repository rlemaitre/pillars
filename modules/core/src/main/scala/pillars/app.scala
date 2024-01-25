package pillars

import cats.effect.*
import cats.effect.std.Console
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
    def infos: AppInfo
    def probes: List[Probe[F]] = Nil
    def run(using p: Pillars[F]): F[Unit]
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

case class AppInfo(name: App.Name, version: App.Version, description: App.Description)
trait BuildInfo:
    def name: String
    def version: String
    def description: String
    def toAppInfo: AppInfo = AppInfo(Name(name.assume), Version(version.assume), Description(description.assume))
end BuildInfo

trait EntryPoint extends IOApp:

    def app: App[IO]
    override final def run(args: List[String]): IO[ExitCode] =
        Command(app.infos.name, app.infos.description)(
          Opts.option[Path]("config", "Path to the configuration file")
        ).parse(
          args,
          sys.env
        ) match
        case Left(help)        => Console[IO].errorln(help).as(ExitCode.Error)
        case Right(configPath) =>
            Pillars(configPath).use: pillars =>
                app.run(using pillars).as(ExitCode.Success)
end EntryPoint
