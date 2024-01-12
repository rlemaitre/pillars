package example

import cats.effect.IO
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.Pillars
import pillars.model.*

case class Bookstore(name: AppName, version: Version, description: Description) extends pillars.App[IO]:
  override def run(pillars: Pillars[IO]): IO[Unit] =
    import pillars.*
    for
      _ <- logger.info(s"📚 Welcome to ${pillars.config.name}!")
      _ <- pillars.apiServer.start(endpoints.all)
    yield ()

object Main
    extends pillars.EntryPoint(Bookstore(AppName("BookStore"), Version("0.0.1"), Description("A simple bookstore")))
