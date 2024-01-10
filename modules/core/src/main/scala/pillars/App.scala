package pillars

import pillars.model.AppName
import pillars.model.Description
import pillars.model.Version

trait App[F[_]]:
  def name: AppName
  def version: Version
  def description: Description
  def run(pillars: Pillars[F]): F[Unit]
