package pillars.config

import io.circe.Codec

final case class AdminConfig(enabled: Boolean, http: HttpServerConfig)

object AdminConfig:
  given Codec[AdminConfig] = Codec.AsObject.derivedConfigured
