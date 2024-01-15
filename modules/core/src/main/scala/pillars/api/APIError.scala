package pillars.api

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.PillarsError
import pillars.PillarsError.Code
import sttp.model.StatusCode

trait APIError extends PillarsError:
    override def status: StatusCode
    final override def code: Code = Code("API")
