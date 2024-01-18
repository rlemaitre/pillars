package pillars.flags

import pillars.AdminServer.baseEndpoint
import sttp.tapir.*
import sttp.tapir.codec.iron.given
import sttp.tapir.json.circe.jsonBody

private[flags] object endpoints:
    private val prefix = baseEndpoint.in("flags")
    def list           = prefix.get.out(jsonBody[List[FeatureFlag]])
    def get            = prefix.get.in(path[FeatureFlag.Name]("name")).out(jsonBody[FeatureFlag])
end endpoints
