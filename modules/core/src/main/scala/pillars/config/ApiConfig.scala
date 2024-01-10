package pillars.config

import io.circe.Codec

final case class ApiConfig(enabled: Boolean, http: HttpServerConfig) derives Codec.AsObject
