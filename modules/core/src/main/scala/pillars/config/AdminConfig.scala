package pillars.config

import io.circe.Decoder
import io.circe.Encoder
import io.circe.derivation.ConfiguredDecoder
import io.circe.derivation.ConfiguredEncoder

final case class AdminConfig() derives ConfiguredEncoder, ConfiguredDecoder
