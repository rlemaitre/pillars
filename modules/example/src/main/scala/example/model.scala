package example

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type UsernameConstraint        = (MinLength[3] & MaxLength[20]) DescribedAs "Must be between 3 and 20 characters"
opaque type Username <: String = String :| UsernameConstraint
object Username extends RefinedTypeOps[String, UsernameConstraint, Username]

type AgeConstraint     = (Positive & Less[150]) DescribedAs "Must be a positive number less than 150"
opaque type Age <: Int = Int :| AgeConstraint
object Age extends RefinedTypeOps[Int, AgeConstraint, Age]

type FirstNameConstraint        = Not[Blank] DescribedAs "First name must not be blank"
opaque type FirstName <: String = String :| FirstNameConstraint
object FirstName extends RefinedTypeOps[String, FirstNameConstraint, FirstName]

type LastNameConstraint        = Not[Blank] DescribedAs "Last name must not be blank"
opaque type LastName <: String = String :| LastNameConstraint
object LastName extends RefinedTypeOps[String, LastNameConstraint, LastName]

type EmailConstraint        = Match[".*@.*\\..*"] DescribedAs "Must be a valid e-mail"
opaque type Email <: String = String :| EmailConstraint
object Email extends RefinedTypeOps[String, EmailConstraint, Email]

type CountryNameConstraint        = Not[Blank] DescribedAs "Country name must not be blank"
opaque type CountryName <: String = String :| CountryNameConstraint
object CountryName extends RefinedTypeOps[String, CountryNameConstraint, CountryName]

type CountryCodeConstraint        = (FixedLength[2] & LettersUpperCase) DescribedAs "Country name must not be blank"
opaque type CountryCode <: String = String :| CountryCodeConstraint
object CountryCode extends RefinedTypeOps[String, CountryCodeConstraint, CountryCode]

case class Country(code: CountryCode, name: CountryName, niceName: String)

case class User(
    firstName: Option[FirstName],
    lastName: Option[LastName],
    email: Email,
    age: Option[Age],
    country: Option[CountryCode]
)
