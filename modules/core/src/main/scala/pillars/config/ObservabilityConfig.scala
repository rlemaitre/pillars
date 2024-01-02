package pillars.config

import io.circe.Codec
import io.github.iltotore.iron.circe.given
import pillars.model.ServiceName

final case class ObservabilityConfig(serviceName: ServiceName) derives Codec.AsObject
