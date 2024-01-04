package example

import io.circe.Codec
import pillars.config.given

case class BookstoreConfig(enabled: Boolean = true, users: UsersConfig = UsersConfig())
object BookstoreConfig:
  given Codec[BookstoreConfig] = Codec.AsObject.derivedConfigured

case class UsersConfig(init: Boolean = false)
object UsersConfig:
  given Codec[UsersConfig] = Codec.AsObject.derivedConfigured
