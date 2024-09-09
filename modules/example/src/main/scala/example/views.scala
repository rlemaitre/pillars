package example

final case class UserView(
    firstName: Option[FirstName],
    lastName: Option[LastName],
    email: Email,
    age: Option[Age],
    country: Option[CountryCode]
):
    def toModel: User = User(firstName, lastName, email, age, country)
end UserView
