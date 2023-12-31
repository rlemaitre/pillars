package pillars.model

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private type AppNameConstraint = Not[Blank]
opaque type AppName <: String  = String :| AppNameConstraint
object AppName extends RefinedTypeOps[String, AppNameConstraint, AppName]

private type VersionConstraint = SemanticVersion
opaque type Version <: String = String :| VersionConstraint
object Version extends RefinedTypeOps[String, VersionConstraint, Version]

private type DescriptionConstraint = Not[Blank]
opaque type Description <: String = String :| DescriptionConstraint
object Description extends RefinedTypeOps[String, DescriptionConstraint, Description]
