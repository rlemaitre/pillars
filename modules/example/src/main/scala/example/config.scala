package example

import io.circe.derivation.ConfiguredDecoder
import io.circe.derivation.ConfiguredEncoder
import pillars.config.given

case class BookstoreConfig(enabled: Boolean = true, users: UsersConfig = UsersConfig())
    derives ConfiguredEncoder,
      ConfiguredDecoder

case class UsersConfig(init: Boolean = false) derives ConfiguredEncoder, ConfiguredDecoder
