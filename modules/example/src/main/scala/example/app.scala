package example

import cats.effect.IO
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.Pillars
import pillars.model.*

object Main extends pillars.EntryPoint:
  def app = new pillars.App[IO]:
    def name        = AppName("BookStore")
    def version     = Version("0.0.1")
    def description = Description("A simple bookstore")

    def run(pillars: Pillars[IO]): IO[Unit] =
      import pillars.*
      for
        _ <- logger.info(s"📚 Welcome to ${pillars.config.name}!")
        _ <- pillars.apiServer.start(endpoints.all)
      yield ()
