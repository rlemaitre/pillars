package pillars.db

import cats.effect.*
import cats.effect.std.Console
import cats.syntax.all.*
import com.comcast.ip4s.*
import fs2.io.file.Files
import fs2.io.net.Network
import io.circe.Codec
import io.circe.Decoder as CirceDecoder
import io.circe.Encoder as CirceEncoder
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
import scala.concurrent.duration.Duration
import scribe.Level
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*
import skunk.util.Typer

extension [F[_]](p: Pillars[F])
    def db: DB[F] = p.module[DB[F]](DB.Key)

final case class DB[F[_]: Async: Network: Tracer: Console](pool: Resource[F, Session[F]]) extends Module[F]:
    export pool.*

    override def probes: List[Probe[F]] =
        val probe = new Probe[F]:
            override def component: Component = Component(Component.Name("db"), Component.Type.Datastore)
            override def check: F[Boolean]    = pool.use(session => session.unique(sql"select true".query(bool)))
        probe.pure[List]
    end probes
end DB

object DB:
    case object Key extends Module.Key:
        override val name: String = "db"
    def apply[F[_]](using p: Pillars[F]): DB[F] = p.module[DB[F]](DB.Key)

class DBLoader extends Loader:
    override type M[F[_]] = DB[F]
    override val key: Module.Key = DB.Key

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
                         debug = config.debug,
                         strategy = config.typerStrategy,
                         parameters = Session.DefaultConnectionParameters ++ config.extraParameters,
                         commandCache = config.commandCache,
                         queryCache = config.queryCache,
                         parseCache = config.parseCache,
                         readTimeout = config.readTimeout,
                         redactionStrategy = config.redactionStrategy,
                         ssl = config.ssl
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
    ssl: SSL = SSL.None,
    systemSchema: DatabaseSchema = DatabaseSchema.public,
    appSchema: DatabaseSchema = DatabaseSchema.public,
    // TODO: Add system and application schemas (default = public)
    poolSize: PoolSize = PoolSize(32),
    debug: Boolean = false,
    probe: ProbeConfig = ProbeConfig(),
    logging: LoggingConfig = LoggingConfig(),
    typerStrategy: Typer.Strategy = Typer.Strategy.BuiltinsOnly,
    extraParameters: Map[String, String] = Map.empty,
    commandCache: Int = 1024,
    queryCache: Int = 1024,
    parseCache: Int = 1024,
    readTimeout: Duration = Duration.Inf,
    redactionStrategy: RedactionStrategy = RedactionStrategy.OptIn
)

object DatabaseConfig:
    given Configuration         = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
    given Codec[DatabaseConfig] = Codec.AsObject.derivedConfigured
    import pillars.Logging.Config.given
    given Codec[LoggingConfig]  = Codec.AsObject.derivedConfigured

    given CirceEncoder[Typer.Strategy] = CirceEncoder.encodeString.contramap:
        case Typer.Strategy.BuiltinsOnly => "BuiltinsOnly"
        case Typer.Strategy.SearchPath   => "SearchPath"
    given CirceDecoder[Typer.Strategy] = CirceDecoder.decodeString.map(_.toLowerCase).emap:
        case "builtinsonly" => Right(Typer.Strategy.BuiltinsOnly)
        case "searchpath"   => Right(Typer.Strategy.SearchPath)
        case other          => Left(s"Invalid Typer strategy: $other")

    given CirceDecoder[SSL] = CirceDecoder.decodeString.map(_.toLowerCase).emap:
        case "none"    => Right(SSL.None)
        case "trusted" => Right(SSL.Trusted)
        case "system"  => Right(SSL.System)
        case other     => Left(s"Invalid SSL mode: $other")
    given CirceEncoder[SSL] = CirceEncoder.encodeString.contramap:
        case SSL.None    => "none"
        case SSL.Trusted => "trusted"
        case SSL.System  => "system"

    given CirceDecoder[RedactionStrategy] = CirceDecoder.decodeString.map(_.toLowerCase).emap:
        case "none"  => Right(RedactionStrategy.None)
        case "all"   => Right(RedactionStrategy.All)
        case "optin" => Right(RedactionStrategy.OptIn)
        case other   => Left(s"Invalid SSL mode: $other")
    given CirceEncoder[RedactionStrategy] = CirceEncoder.encodeString.contramap:
        case RedactionStrategy.None  => "none"
        case RedactionStrategy.All   => "all"
        case RedactionStrategy.OptIn => "OptIn"

end DatabaseConfig

final case class LoggingConfig(
    enabled: Boolean = false,
    level: Level = Level.Debug,
    statements: Boolean = false,
    timing: Boolean = false
)

private type DatabaseNameConstraint = Not[Blank] DescribedAs "Database name must not be blank"
opaque type DatabaseName <: String  = String :| DatabaseNameConstraint

object DatabaseName extends RefinedTypeOps[String, DatabaseNameConstraint, DatabaseName]

private type DatabaseSchemaConstraint = Not[Blank] DescribedAs "Database schema must not be blank"
opaque type DatabaseSchema <: String  = String :| DatabaseSchemaConstraint

object DatabaseSchema extends RefinedTypeOps[String, DatabaseSchemaConstraint, DatabaseSchema]:
    val public: DatabaseSchema  = DatabaseSchema("public")
    val pillars: DatabaseSchema = DatabaseSchema("pillars")

private type DatabaseTableConstraint =
    (Not[Blank] & Match["""^[a-zA-Z_][0-9a-zA-Z$_]{0,63}$"""]) DescribedAs "Database table must be at most 64 characters (letter, digit, dollar sign or underscore) long and start with a letter or an underscore"
opaque type DatabaseTable <: String  = String :| DatabaseTableConstraint

object DatabaseTable extends RefinedTypeOps[String, DatabaseTableConstraint, DatabaseTable]

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
