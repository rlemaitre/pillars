package example

import cats.effect.*
import example.build.BuildInfo
import io.github.iltotore.iron.*
import pillars.*
import pillars.db.*
import pillars.db.migrations.*
import pillars.flags.*
import pillars.flags.flag
import pillars.httpclient.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

// tag::quick-start[]
object app extends pillars.EntryPoint: // // <1>
    def app: pillars.App[IO] = new: // // <2>
        def infos: AppInfo = BuildInfo.toAppInfo // // <3>

        def run: Run[IO, IO[Unit]] = // // <4>
            for
                _ <- logger.info(s"📚 Welcome to ${config.name}!")
                _ <- dbMigration.migrate("classpath:db-migrations") // // <5>
                _ <- flag"feature-1".whenEnabled:
                         sessions.use: session =>
                             for
                                 date <- session.unique(sql"select now()".query(timestamptz))
                                 _    <- logger.info(s"The current date is $date.")
                             yield ()
                _ <- http.get("https://swapi.dev/api/people/1"): response =>
                         for
                             _    <- logger.info(s"Response: ${response.status}")
                             size <- response.body.compile.count
                             _    <- logger.info(s"Body: $size bytes")
                         yield ()
                _ <- server.start(homeController, userController)   // // <6>
            yield ()
            end for
        end run
end app
// end::quick-start[]
