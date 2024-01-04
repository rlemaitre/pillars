package pillars.config

import io.circe.*
import pillars.logging.Log
import scribe.Level

final case class LogConfig(
    level: Level = Level.Info,
    format: Log.Format = Log.Format.Enhanced,
    output: Log.Output = Log.Output.Console,
    excludeHikari: Boolean = false
)
object LogConfig:
  given Codec[LogConfig] = Codec.AsObject.derivedConfigured
  given Decoder[Level]   = Decoder.decodeString.emap(s => Level.get(s).toRight(s"Invalid log level $s"))
  given Encoder[Level]   = Encoder.encodeString.contramap(_.toString)
