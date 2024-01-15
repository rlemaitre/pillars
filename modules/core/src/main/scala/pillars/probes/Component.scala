package pillars.probes

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.probes.Component.Name

final case class Component(name: Name, `type`: Component.Type, description: Option[String] = None)

object Component:
    private type NameConstraint = Not[Blank] & Not[Contain[":"]]
    opaque type Name <: String  = String :| NameConstraint
    object Name extends RefinedTypeOps[String, NameConstraint, Name]

    enum Type:
        case System
        case Datastore
        case Component
    end Type
end Component
