package pillars.admin.views

import io.circe.Codec
import pillars.json.probes.given
import pillars.probes.Component
import pillars.probes.Status
import sttp.tapir.*

final case class HealthStatus(
    status: Status,
    checks: List[CheckStatus]
) derives Codec.AsObject,
      Schema

final case class CheckStatus(
    componentId: String,
    componentType: Component.Type,
    status: Status
) derives Codec.AsObject,
      Schema
