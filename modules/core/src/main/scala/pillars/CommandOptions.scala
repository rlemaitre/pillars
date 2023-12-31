package pillars

import cats.data.Validated
import com.monovore.decline.Opts
import java.nio.file.Path
import scala.util.Try
import scribe.Level

object CommandOptions:
  val logLevel: Opts[Level] = Opts
    .option[String]("log-level", "Set the log level")
    .mapValidated: string =>
      Try(Level(string)).toEither match
        case Right(value) => Validated.valid(value)
        case Left(value)  => Validated.invalidNel(value.getMessage)
    .withDefault(Level.Info)

  val config: Opts[Option[Path]] = Opts
    .option[Path]("config", "Path to the configuration file")
    .orNone
