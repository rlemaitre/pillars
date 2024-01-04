package pillars.config

import io.circe.Codec
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.given
import pillars.model.ServiceName

final case class ObservabilityConfig(serviceName: ServiceName = ServiceName("pillars"))

object ObservabilityConfig:
  given Codec[ObservabilityConfig] = Codec.AsObject.derivedConfigured
