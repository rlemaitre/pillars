package example

import cats.effect.*
import example.build.BuildInfo
import io.github.iltotore.iron.*
import pillars.*
import pillars.db.*
import pillars.db.migrations.DBMigration
import pillars.flags.*
import pillars.httpclient.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

// tag::quick-start[]
object app extends pillars.EntryPoint: // // <1>
    def app: pillars.App[IO] = new: // // <2>
        def infos: AppInfo = BuildInfo.toAppInfo // // <3>

        def endpoints = TodoController().endpoints.map(_.endpoint)

        def run: Run[IO, IO[Unit]] = // // <4>
            for
                _ <- Logger[IO].info(s"📚 Welcome to ${Config[IO].name}!")
                _ <- DBMigration[IO].migrate("db-migrations") // // <5>
                _ <- flag"feature-1".whenEnabled:
                         DB[IO].use: session =>
                             for
                                 date <- session.unique(sql"select now()".query(timestamptz))
                                 _    <- Logger[IO].info(s"The current date is $date.")
                             yield ()
                _ <- HttpClient[IO].get("https://pillars.rlemaitre.com"): response =>
                         for
                             _    <- Logger[IO].info(s"Response: ${response.status}")
                             size <- response.body.compile.count
                             _    <- Logger[IO].info(s"Body: $size bytes")
                         yield ()
                _ <- ApiServer[IO].start(TodoController().endpoints)
            yield ()
            end for
        end run
end app
// end::quick-start[]
