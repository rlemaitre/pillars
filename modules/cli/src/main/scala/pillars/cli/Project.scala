package pillars.cli

import Project.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class Project(
    name: Name
)

object Project:
    private type NameConstraint = Not[Blank] DescribedAs "Project name must not be blank"
    opaque type Name <: String  = String :| NameConstraint
    object Name extends RefinedTypeOps[String, NameConstraint, Name]
