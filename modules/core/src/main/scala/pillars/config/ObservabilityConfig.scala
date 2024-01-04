package pillars.config

import io.circe.Decoder
import io.circe.Encoder
import io.circe.derivation.ConfiguredDecoder
import io.circe.derivation.ConfiguredEncoder
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.given
import pillars.model.ServiceName

final case class ObservabilityConfig(serviceName: ServiceName = ServiceName("pillars"))
    derives ConfiguredEncoder,
      ConfiguredDecoder
