package pillars.config

import cats.syntax.all.*
import pillars.logging.Log
import scribe.Level

final case class LogConfig(
    level: Level = Level.Info,
    format: Log.Format = Log.Format.Enhanced,
    output: Log.Output = Log.Output.Console,
    excludeHikari: Boolean = false
)
