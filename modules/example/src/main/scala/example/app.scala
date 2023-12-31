package example

import cats.effect.IO
import cats.effect.ExitCode
import com.monovore.decline.Command
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.Pillars
import pillars.model.*

case class BookStore(name: AppName, version: Version, description: Description) extends pillars.App[IO]:
  override type ConfigType = BookstoreConfig
  override def run(pillars: Pillars[IO]): IO[Unit] = ???
  override def command: Command[IO[ExitCode]]      = ???

object Main
    extends pillars.EntryPoint(
      BookStore(AppName("BookStore"), Version("0.0.1"), Description("A simple bookstore"))
    )
