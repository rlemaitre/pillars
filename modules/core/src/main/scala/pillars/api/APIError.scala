package pillars.api

import scala.util.control.NoStackTrace
import sttp.model.StatusCode

trait APIError extends Throwable, NoStackTrace:
  def statusCode: StatusCode
