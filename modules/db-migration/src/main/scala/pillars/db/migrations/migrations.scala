package pillars.db.migrations

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import cats.syntax.all.*
import dumbo.ConnectionConfig
import dumbo.Dumbo
import dumbo.Dumbo.MigrationResult
import dumbo.DumboWithResourcesPartiallyApplied
import fs2.io.file.Files
import fs2.io.net.Network
import io.circe.Codec
import io.circe.derivation.Configuration
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import mouse.all.anySyntaxMouse
import org.typelevel.otel4s.trace.Tracer
import pillars.Loader
import pillars.Logger
import pillars.Module
import pillars.Modules
import pillars.Pillars
import pillars.Run
import pillars.codec.given
import pillars.db.DatabaseConfig
import pillars.db.DatabaseSchema
import pillars.db.DatabaseTable
import pillars.db.DB
import scala.concurrent.duration.*

final case class DBMigration[F[_]: Async: Console: Tracer: Network: Files](
    config: MigrationConfig,
    dbConfig: DatabaseConfig
) extends Module[F]:
    private val connectionConfig = dumbo.ConnectionConfig(
      host = dbConfig.host.toString,
      port = dbConfig.port.value,
      database = dbConfig.database,
      user = dbConfig.username,
      password = dbConfig.password.value.some,
      ssl = dbConfig.ssl
    )

    inline def migrateModule(key: Module.Key): Run[F, F[Unit]] =
        migrate(
          "db/migrations",
          DatabaseSchema.pillars,
          DatabaseTable(s"${key.name.replaceAll("[^0-9a-zA-Z$_]", "-")}_schema_history".assume)
        )
    inline def migrate(
        path: String,
        schema: DatabaseSchema = DatabaseSchema.public,
        schemaHistoryTable: DatabaseTable = DatabaseTable(Dumbo.defaults.schemaHistoryTable.assume)
    ): Run[F, F[Unit]] = (Dumbo.withResourcesIn(path) |> migrate(schema, schemaHistoryTable)).flatMap: result =>
        Logger[F].info(s"Migration completed with ${result.migrationsExecuted} migrations executed")

    private def migrate(
        schema: DatabaseSchema,
        table: DatabaseTable
    )(dumbo: DumboWithResourcesPartiallyApplied[F]): F[MigrationResult] =
        dumbo.withMigrationStateLogAfter(config.logAfter).apply(
          connection = connectionConfig,
          defaultSchema = schema,
          schemas = Set(dbConfig.appSchema),
          schemaHistoryTable = table,
          validateOnMigrate = config.validateOnMigrate
        ).runMigration

    end migrate
end DBMigration

object DBMigration:
    def apply[F[_]](using p: Pillars[F]): DBMigration[F] = p.module[DBMigration[F]](DBMigration.Key)

    case object Key extends Module.Key:
        override val name: String = "db-migration"
end DBMigration

class DBMigrationLoader extends Loader:
    override type M[F[_]] = DBMigration[F]
    override val key: Module.Key = DBMigration.Key

    override def dependsOn: Set[Module.Key] = Set(DB.Key)

    def load[F[_]: Async: Network: Tracer: Console](
        context: Loader.Context[F],
        modules: Modules[F]
    ): Resource[F, DBMigration[F]] =
        import context.*
        given Files[F] = Files.forAsync[F]
        Resource.eval:
            for
                _        <- logger.info("Loading DB Migration module")
                dbConfig <- configReader.read[DatabaseConfig]("db")
                config   <- configReader.read[MigrationConfig]("db-migration")
                _        <- logger.info("DB Migration module loaded")
            yield DBMigration(config, dbConfig)
            end for
    end load

end DBMigrationLoader

final case class MigrationConfig(
    logAfter: FiniteDuration = 5.seconds,
    validateOnMigrate: Boolean = Dumbo.defaults.validateOnMigrate
)
object MigrationConfig:
    given Configuration = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults

    given Codec[MigrationConfig] = Codec.AsObject.derivedConfigured

extension [F[_]](p: Pillars[F])
    def dbMigration: DBMigration[F] = p.module(DBMigration.Key)

end extension
