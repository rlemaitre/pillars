package example

import cats.effect.IO
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.Pillars
import pillars.db.DB
import pillars.model.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

object Main extends pillars.EntryPoint:
    def app: pillars.App[IO] = new pillars.App[IO]:
        def name        = AppName("BookStore")
        def version     = Version("0.0.1")
        def description = Description("A simple bookstore")

        def run(pillars: Pillars[IO]): IO[Unit] =
            import pillars.*
            for
                _ <- logger.info(s"📚 Welcome to ${pillars.config.name}!")
                _ <- module[DB[IO]].use: s =>
                         for
                             d <- s.unique(sql"select now()".query(timestamptz))
                             _ <- logger.info(s"The current date is $d.")
                         yield ()
                _ <- pillars.apiServer.start(endpoints.all)
            yield ()
            end for
        end run
end Main
