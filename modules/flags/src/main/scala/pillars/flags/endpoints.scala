package pillars.flags

import pillars.admin.endpoints.baseEndpoint
import sttp.tapir.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.codec.iron.given

object endpoints:
    private val prefix = baseEndpoint.in("flags")
    def list           = prefix.get.out(jsonBody[List[FeatureFlag]])
    def get            = prefix.get.in(path[FeatureFlag.Name]("name")).out(jsonBody[FeatureFlag])
    def all            = List(list, get)
end endpoints
