package pillars

import io.circe.Decoder
import pillars.model.AppName
import pillars.model.Description
import pillars.model.Version

trait App[F[_], T: Decoder]:
  type Config = T
  def name: AppName
  def version: Version
  def description: Description
  def run(pillars: Pillars[F, Config]): F[Unit]
