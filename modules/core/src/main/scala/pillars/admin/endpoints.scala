package pillars.admin

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.admin.views.ErrorView
import pillars.admin.views.HealthStatus
import pillars.json.flags.given
import pillars.model.FeatureFlag
import sttp.model.Header
import sttp.model.HeaderNames
import sttp.tapir.*
import sttp.tapir.Schema
import sttp.tapir.codec.iron.*
import sttp.tapir.codec.iron.given
import sttp.tapir.json.circe.jsonBody
object endpoints:
    private val baseEndpoint = endpoint.in("admin").errorOut(ErrorView.output)
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
    object flags:
        private val prefix = baseEndpoint.in("flags")
        def list           = prefix.get.out(jsonBody[List[FeatureFlag]])
        def get            = prefix.get.in(path[FeatureFlag.Name]("name")).out(jsonBody[FeatureFlag])
        def all            = List(list, get)
    end flags
end endpoints
