package pillars.db.migration

import cats.effect.IO
import cats.syntax.all.*
import com.comcast.ip4s.*
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.munit.TestContainerForEach
import io.circe.Decoder
import io.github.iltotore.iron.*
import munit.CatsEffectSuite
import org.testcontainers.utility.DockerImageName
import org.typelevel.otel4s.trace.Tracer
import pillars.Config.Secret
import pillars.Module
import pillars.Pillars
import pillars.db.*
import pillars.db.migrations.DBMigration
import pillars.db.migrations.MigrationConfig
import pillars.probes.ProbeConfig
import scala.concurrent.duration.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

class MigrationTests extends CatsEffectSuite, TestContainerForEach:

    override val containerDef: PostgreSQLContainer.Def = PostgreSQLContainer.Def(
      dockerImageName = DockerImageName.parse("postgres:16.2"),
      databaseName = "pillars-migration",
      username = "pillars",
      password = "pillars"
    )

    given Pillars[IO] = new Pillars[IO]:
        def observability                   = ???
        def config                          = ???
        def apiServer                       = ???
        def logger                          = scribe.cats.io
        def readConfig[T](using Decoder[T]) = ???
        def module[T](key: Module.Key): T   = ???

    given Tracer[IO]                                                        = Tracer.noop[IO]
    private def configFor(pgContainer: PostgreSQLContainer): DatabaseConfig =
        DatabaseConfig(
          host = Host.fromString(pgContainer.host).get,
          port = Port.fromInt(pgContainer.container.getMappedPort(5432)).get,
          database = DatabaseName(pgContainer.databaseName.assume),
          username = DatabaseUser(pgContainer.username.assume),
          password = Secret(DatabasePassword(pgContainer.password.assume)),
          poolSize = PoolSize(10),
          probe = ProbeConfig()
        )

    test("migration should run the scripts"):
        withContainers { pgContainer =>
            val config: MigrationConfig  = MigrationConfig(
              logAfter = 1.second,
              validateOnMigrate = true
            )
            val dbConfig: DatabaseConfig = configFor(pgContainer)
            val migration                = DBMigration[IO](config, dbConfig)
            val result                   =
                for
                    _   <- migration.migrate("db/migrations")
                    res <- session(dbConfig).use: s =>
                               s.unique(sql"SELECT count(*) FROM test where d is not null".query(int8))
                yield res
            assertIO(result, 5L)
        }

    test("migration should write in the history table"):
        withContainers { pgContainer =>
            val config: MigrationConfig  = MigrationConfig(
              logAfter = 1.second,
              validateOnMigrate = true
            )
            val dbConfig: DatabaseConfig = configFor(pgContainer)
            val migration                = DBMigration[IO](config, dbConfig)
            val result                   =
                for
                    _   <- migration.migrate("db/migrations", DatabaseSchema.public, DatabaseTable("schema_history"))
                    res <- session(dbConfig).use: s =>
                               s.unique(sql"SELECT count(*) FROM schema_history".query(int8))
                yield res
            assertIO(result, 3L) // 1 for init and one for each migration file
        }

    test("running twice migrations should be the same as running once"):
        withContainers { pgContainer =>
            val config: MigrationConfig  = MigrationConfig(
              logAfter = 1.second,
              validateOnMigrate = true
            )
            val dbConfig: DatabaseConfig = configFor(pgContainer)
            val migration                = DBMigration[IO](config, dbConfig)
            val result                   =
                for
                    _   <- migration.migrate("db/migrations")
                    _   <- migration.migrate("db/migrations")
                    res <- session(dbConfig).use: s =>
                               s.unique(sql"SELECT count(*) FROM test where d is not null".query(int8))
                yield res
            assertIO(result, 5L)
        }

    private def session(dbConfig: DatabaseConfig) =
        Session.single[IO](
          host = dbConfig.host.toString,
          port = dbConfig.port.value,
          database = dbConfig.database,
          user = dbConfig.username,
          password = dbConfig.password.some.map(_.value),
          debug = dbConfig.debug
        )
end MigrationTests
