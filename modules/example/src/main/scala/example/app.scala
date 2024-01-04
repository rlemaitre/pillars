package example

import cats.effect.IO
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.Pillars
import pillars.model.*

case class Bookstore(name: AppName, version: Version, description: Description)
    extends pillars.App[IO, BookstoreConfig]:
  override type Config = BookstoreConfig
  override def run(pillars: Pillars[IO, BookstoreConfig]): IO[Unit] =
    for
      _ <- pillars.logger.info(s"📚 Welcome to ${pillars.config.name}!")
      _ <- pillars.logger.debug(s"📖 Enabled: ${pillars.config.app.enabled}")
    yield ()

object Main
    extends pillars.EntryPoint(Bookstore(AppName("BookStore"), Version("0.0.1"), Description("A simple bookstore")))
