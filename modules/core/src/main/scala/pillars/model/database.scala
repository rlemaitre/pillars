package pillars.model

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private type DatabaseNameConstraint = Not[Blank] DescribedAs "Database name must not be blank"
opaque type DatabaseName <: String  = String :| DatabaseNameConstraint
object DatabaseName extends RefinedTypeOps[String, DatabaseNameConstraint, DatabaseName]

private type DatabaseSchemaConstraint = Not[Blank] DescribedAs "Database schema must not be blank"
opaque type DatabaseSchema <: String  = String :| DatabaseSchemaConstraint
object DatabaseSchema extends RefinedTypeOps[String, DatabaseSchemaConstraint, DatabaseSchema]

private type DatabaseUserConstraint = Not[Blank] DescribedAs "Database user must not be blank"
opaque type DatabaseUser <: String  = String :| DatabaseUserConstraint
object DatabaseUser extends RefinedTypeOps[String, DatabaseUserConstraint, DatabaseUser]

private type DatabasePasswordConstraint = Not[Blank] DescribedAs "Database password must not be blank"
opaque type DatabasePassword <: String  = String :| DatabasePasswordConstraint
object DatabasePassword extends RefinedTypeOps[String, DatabasePasswordConstraint, DatabasePassword]

private type PoolSizeConstraint = GreaterEqual[1] DescribedAs "Pool size must be greater or equal to 1"
opaque type PoolSize <: Int     = Int :| PoolSizeConstraint
object PoolSize extends RefinedTypeOps[Int, PoolSizeConstraint, PoolSize]

private type VersionConstraint = Not[Blank] & Match["^(\\d+\\.\\d+\\.\\d+)$"] DescribedAs
  "Schema version must be in the form of X.Y.Z"
opaque type SchemaVersion <: String = String :| VersionConstraint
object SchemaVersion extends RefinedTypeOps[String, Not[Blank] & Match["^(\\d+\\.\\d+\\.\\d+)$"], SchemaVersion]
