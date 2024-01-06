package pillars.config

import io.circe.Codec

final case class ApiConfig(enabled: Boolean, http: HttpServerConfig)

object ApiConfig:
  given Codec[ApiConfig] = Codec.AsObject.derivedConfigured
