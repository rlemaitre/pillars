package pillars.config

import scala.util.control.NoStackTrace

enum ConfigError extends Throwable, NoStackTrace:
  case MissingEnvironmentVariable(name: String)
  case ParsingError(cause: Throwable)
