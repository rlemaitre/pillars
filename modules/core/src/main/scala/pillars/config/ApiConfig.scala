package pillars.config

import io.circe.Decoder
import io.circe.Encoder
import io.circe.derivation.ConfiguredDecoder
import io.circe.derivation.ConfiguredEncoder

final case class ApiConfig() derives ConfiguredEncoder, ConfiguredDecoder
