package example

import io.circe.generic.semiauto.deriveCodec
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import sttp.tapir.Schema
import sttp.tapir.codec.iron.given
object codec:
    object db:
        import skunk.*
        import skunk.codec.all.*

        val countryCode: Codec[CountryCode] = varchar(2).eimap(CountryCode.either)(_.value)
        val countryName: Codec[CountryName] = varchar.eimap(CountryName.either)(_.value)
        val country: Codec[Country]         =
            (countryCode *: countryName *: text).imap(Country.apply)(c => (c.code, c.name, c.niceName))

        val firstName: Codec[FirstName] = text.eimap(FirstName.either)(_.value)
        val lastName: Codec[LastName]   = text.eimap(LastName.either)(_.value)
        val email: Codec[Email]         = text.eimap(Email.either)(_.value)
        val age: Codec[Age]             = int4.eimap(Age.either)(_.value)
        val user: Codec[User]           =
            (firstName.opt *: lastName.opt *: email *: age.opt *: countryCode.opt).imap(User.apply)(u =>
                (u.firstName, u.lastName, u.email, u.age, u.country)
            )
    end db

    object json:
        import io.circe.*
        given Codec[UserView]  = deriveCodec[UserView]
        given Schema[UserView] = Schema.derived
end codec
