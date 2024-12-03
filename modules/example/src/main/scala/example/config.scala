// Copyright (c) 2024-2024 by Raphaël Lemaitre and Contributors
// This software is licensed under the Eclipse Public License v2.0 (EPL-2.0).
// For more information see LICENSE or https://opensource.org/license/epl-2-0

package example

import io.circe.Codec

case class BookstoreConfig(enabled: Boolean = true, users: UsersConfig = UsersConfig())
object BookstoreConfig:
    given Codec[BookstoreConfig] = Codec.AsObject.derived

case class UsersConfig(init: Boolean = false)
object UsersConfig:
    given Codec[UsersConfig] = Codec.AsObject.derived
