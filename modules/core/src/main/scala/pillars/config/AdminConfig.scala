package pillars.config

import io.circe.Codec

final case class AdminConfig()

object AdminConfig:
  given Codec[AdminConfig] = Codec.AsObject.derivedConfigured
