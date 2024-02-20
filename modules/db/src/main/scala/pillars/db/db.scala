package pillars.db

import cats.effect.*
import cats.effect.std.Console
import cats.syntax.all.*
import com.comcast.ip4s.*
import fs2.io.file.Files
import fs2.io.net.Network
import io.circe.Codec
import io.circe.derivation.Configuration
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import org.typelevel.otel4s.trace.Tracer
import pillars.Config.*
import pillars.Loader
import pillars.Module
import pillars.Modules
import pillars.Pillars
import pillars.codec.given
import pillars.probes.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

extension [F[_]](p: Pillars[F])
    def db: DB[F] = p.module[DB[F]](DB.Key)

final case class DB[F[_]: Async: Network: Tracer: Console](pool: Resource[F, Session[F]]) extends Module[F]:
    export pool.*

    override def key: Module.Key        = DB.Key
    override def probes: List[Probe[F]] =
        val probe = new Probe[F]:
            override def component: Component = Component(Component.Name("db"), Component.Type.Datastore)
            override def check: F[Boolean]    = pool.use(session => session.unique(sql"select true".query(bool)))
        probe.pure[List]
    end probes
end DB

object DB:
    case object Key extends Module.Key
    def apply[F[_]](using p: Pillars[F]): DB[F] = p.module[DB[F]](DB.Key)

class DBLoader extends Loader:
    override type M[F[_]] = DB[F]

    override def name: String = "db"

    def load[F[_]: Async: Network: Tracer: Console](
        context: Loader.Context[F],
        modules: Modules[F]
    ): Resource[F, DB[F]] =
        import context.*
        given Files[F] = Files.forAsync[F]
        for
            _       <- Resource.eval(logger.info("Loading DB module"))
            config  <- Resource.eval(configReader.read[DatabaseConfig]("db"))
            poolRes <- Session.pooled[F](
                         host = config.host.toString,
                         port = config.port.value,
                         database = config.database,
                         user = config.username,
                         password = config.password.some.map(_.value),
                         max = config.poolSize,
                         debug = config.debug
                       )
            _       <- Resource.eval(logger.info("DB module loaded"))
        yield DB(poolRes)
        end for
    end load
end DBLoader

final case class DatabaseConfig(
    host: Host = host"localhost",
    port: Port = port"5432",
    database: DatabaseName,
    username: DatabaseUser,
    password: Secret[DatabasePassword],
    poolSize: PoolSize = PoolSize(32),
    debug: Boolean = false,
    probe: ProbeConfig
)

object DatabaseConfig:
    given Configuration         = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
    given Codec[DatabaseConfig] = Codec.AsObject.derivedConfigured

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

private type VersionConstraint      = Not[Blank] & Match["^(\\d+\\.\\d+\\.\\d+)$"] DescribedAs
    "Schema version must be in the form of X.Y.Z"
opaque type SchemaVersion <: String = String :| VersionConstraint

object SchemaVersion extends RefinedTypeOps[String, Not[Blank] & Match["^(\\d+\\.\\d+\\.\\d+)$"], SchemaVersion]
