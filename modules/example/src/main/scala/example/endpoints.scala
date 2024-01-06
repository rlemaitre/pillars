package example
import pillars.http.HttpEndpoint

object endpoints:
  val all: List[HttpEndpoint] = pillars.admin.endpoints.all
