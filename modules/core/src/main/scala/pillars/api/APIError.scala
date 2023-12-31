package pillars.api

import sttp.model.StatusCode

import scala.util.control.NoStackTrace

trait APIError extends Throwable, NoStackTrace:
  def statusCode: StatusCode