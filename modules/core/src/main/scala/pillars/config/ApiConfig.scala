package pillars.config

import io.circe.Codec

final case class ApiConfig()

object ApiConfig:
  given Codec[ApiConfig] = Codec.AsObject.derivedConfigured
