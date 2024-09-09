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
