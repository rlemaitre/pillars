package pillars.config

import cats.syntax.all.*
import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.circe.derivation.Configuration
import pillars.logging.Log
import scribe.Level

final case class LogConfig(
    level: Level = Level.Info,
    format: Log.Format = Log.Format.Enhanced,
    output: Log.Output = Log.Output.Console,
    excludeHikari: Boolean = false
)
object LogConfig:
  given Configuration    = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
  given Decoder[Level]   = Decoder.decodeString.emap(s => Level.get(s).toRight(s"Invalid log level $s"))
  given Encoder[Level]   = Encoder.encodeString.contramap(_.toString)
  given Codec[LogConfig] = Codec.AsObject.derivedConfigured
