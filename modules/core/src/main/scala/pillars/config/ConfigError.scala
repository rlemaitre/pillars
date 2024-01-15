package pillars.config

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.PillarsError
import pillars.PillarsError.Code
import pillars.PillarsError.ErrorNumber
import pillars.PillarsError.Message

enum ConfigError(val number: ErrorNumber) extends PillarsError:
    override def code: Code = Code("CONF")
    case MissingEnvironmentVariable(name: String) extends ConfigError(ErrorNumber(1))
    case ParsingError(cause: Throwable)           extends ConfigError(ErrorNumber(2))

    override def message: Message = this match
    case ConfigError.MissingEnvironmentVariable(name) => Message(s"Missing environment variable $name".assume)
    case ConfigError.ParsingError(cause)              => Message(s"Failed to parse configuration: ${cause.getMessage}".assume)
end ConfigError
