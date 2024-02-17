package pillars.flags

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class FeatureFlag(name: Flag, status: Status):
    def isEnabled: Boolean = status.isEnabled

private type FlagConstraint = Not[Blank] DescribedAs "Name must not be blank"
opaque type Flag <: String  = String :| FlagConstraint

object Flag extends RefinedTypeOps[String, FlagConstraint, Flag]

enum Status:
    case Enabled, Disabled

    def isEnabled: Boolean = this match
    case Enabled  => true
    case Disabled => false
end Status
