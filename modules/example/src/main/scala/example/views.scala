package example

final case class UserView(
    firstName: Option[FirstName],
    lastName: Option[LastName],
    email: Email,
    age: Option[Age],
    country: Option[CountryCode]
)
