package example

import io.circe.Codec

case class BookstoreConfig(enabled: Boolean = true, users: UsersConfig = UsersConfig())
object BookstoreConfig:
    given Codec[BookstoreConfig] = Codec.AsObject.derived

case class UsersConfig(init: Boolean = false)
object UsersConfig:
    given Codec[UsersConfig] = Codec.AsObject.derived
