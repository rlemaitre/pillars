package pillars.config

import io.github.iltotore.iron.given
import pillars.model.ServiceName

final case class ObservabilityConfig(serviceName: ServiceName = ServiceName("pillars"))
