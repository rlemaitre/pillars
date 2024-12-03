// Copyright (c) 2024-2024 by Raphaël Lemaitre and Contributors
// This software is licensed under the Eclipse Public License v2.0 (EPL-2.0).
// For more information see LICENSE or https://opensource.org/license/epl-2-0

package example

import example.codec.db.*
import skunk.Command
import skunk.Query
import skunk.Void
import skunk.implicits.*

object db:
    object users:
        val createUser: Command[User] = sql"""
             insert into user_registry (first_name, last_name, email, age, country)
             values (${firstName.opt}, ${lastName.opt}, $email, ${age.opt}, ${countryCode.opt})
             """
            .command
            .to[User]

        val listUsers: Query[Void, User] = sql"""
             select first_name, last_name, email, age, country
             from user_registry
             """.query(codec.db.user)
    end users
end db
