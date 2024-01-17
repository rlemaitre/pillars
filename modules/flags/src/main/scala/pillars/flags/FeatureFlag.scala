package pillars.flags

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.flags.FeatureFlag.Name
import pillars.flags.FeatureFlag.Status

final case class FeatureFlag(name: Name, status: Status):
    def isEnabled: Boolean = status.isEnabled
object FeatureFlag:
    private type NameConstraint = Not[Blank] DescribedAs "Name must not be blank"
    opaque type Name <: String  = String :| NameConstraint
    object Name extends RefinedTypeOps[String, NameConstraint, Name]

    enum Status:
        case Enabled, Disabled
        def isEnabled: Boolean = this match
        case Enabled  => true
        case Disabled => false
    end Status
end FeatureFlag
