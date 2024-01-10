package pillars.config

import io.circe.Codec

final case class AdminConfig(enabled: Boolean, http: HttpServerConfig) derives Codec.AsObject
