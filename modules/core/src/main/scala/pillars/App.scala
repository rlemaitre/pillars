package pillars

import cats.effect.ExitCode
import com.monovore.decline.Command
import pillars.model.AppName
import pillars.model.Description
import pillars.model.Version

trait App[F[_]]:
  type ConfigType
  def name: AppName
  def version: Version
  def description: Description
  def run(pillars: Pillars[F]): F[Unit]
  def command: Command[F[ExitCode]]
