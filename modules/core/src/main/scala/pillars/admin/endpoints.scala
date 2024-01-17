package pillars.admin

import pillars.admin.views.ErrorView
import pillars.admin.views.HealthStatus
import sttp.model.Header
import sttp.model.HeaderNames
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.json.circe.jsonBody
object endpoints:
    val baseEndpoint = endpoint.in("admin").errorOut(ErrorView.output)
    object probes:
        private val prefix = baseEndpoint.in("probes")
        def liveness       = prefix.get.in("healthz").out(stringBody)
        def readiness      =
            prefix.get
                .in("health")
                .out(jsonBody[HealthStatus])
                .out(header(Header(HeaderNames.ContentType, "application/health+json")))
        def all            = List(liveness, readiness)
    end probes
end endpoints
