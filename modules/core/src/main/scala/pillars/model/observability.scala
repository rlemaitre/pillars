package pillars.model

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*


private type ServiceNameConstraint = Not[Blank]
opaque type ServiceName <: String = String :| ServiceNameConstraint
object ServiceName extends RefinedTypeOps[String, ServiceNameConstraint, ServiceName]
