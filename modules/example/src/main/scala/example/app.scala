package example

import cats.effect.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.*
import pillars.App.*
import pillars.db.*
import pillars.flags.*
import pillars.httpclient.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

// tag::quick-start[]
object app extends pillars.EntryPoint: // // <1>
    def app: pillars.App[IO] = new pillars.App[IO]: // // <2>
        def name        = Name("BookStore")
        def version     = Version("0.0.1")
        def description = Description("A simple bookstore")

        def run(using p: Pillars[IO]): IO[Unit] = // // <3>
            import p.*
            for
                _ <- logger.info(s"📚 Welcome to ${config.name}!")
                _ <- flag"feature-1".whenEnabled:
                         DB[IO].use: session =>
                             for
                                 date <- session.unique(sql"select now()".query(timestamptz))
                                 _    <- logger.info(s"The current date is $date.")
                             yield ()
                _ <- HttpClient[IO].get("https://pillars.rlemaitre.com"): response =>
                         logger.info(s"Response: ${response.status}")
                _ <- apiServer.start(endpoints.all)
            yield ()
            end for
        end run
end app
// end::quick-start[]
