package example

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import java.time.*

opaque type Username <: String = String :| MinLength[3] & MaxLength[20]
object Username extends RefinedTypeOps[String, MinLength[3] & MaxLength[20], Username]

opaque type Age <: Int = Int :| Positive & Less[150]
object Age extends RefinedTypeOps[Int, Positive & Less[150], Age]

opaque type Title <: String = String :| Not[Blank]
object Title extends RefinedTypeOps[String, Not[Blank], Title]

opaque type FirstName <: String = String :| Not[Blank]
object FirstName extends RefinedTypeOps[String, Not[Blank], FirstName]

opaque type LastName <: String = String :| Not[Blank]
object LastName extends RefinedTypeOps[String, Not[Blank], LastName]

case class Book(title: Title, authors: List[Author], year: Year, pages: Int)

case class Author(firstName: FirstName, lastName: LastName)

case class User(name: Username, age: Age)
