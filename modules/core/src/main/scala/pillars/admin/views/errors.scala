package pillars.admin.views

import io.circe.Codec
import pillars.PillarsError
import sttp.model.StatusCode
import sttp.tapir.EndpointOutput
import sttp.tapir.Schema
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.statusCode

def errorView[T](error: PillarsError): Either[(StatusCode, ErrorView), T] =
  Left((error.status, ErrorView(f"${error.code}-${error.number}%04d", error.message, error.details)))

case class ErrorView(code: String, message: String, details: Option[String]) derives Codec.AsObject, Schema
object ErrorView:
  val output: EndpointOutput[(StatusCode, ErrorView)] = statusCode.and(jsonBody[ErrorView])
