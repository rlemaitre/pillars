package pillars

import io.circe.derivation.ConfiguredDecoder
import pillars.model.AppName
import pillars.model.Description
import pillars.model.Version

trait App[F[_], T: ConfiguredDecoder]:
  type Config = T
  def name: AppName
  def version: Version
  def description: Description
  def run(pillars: Pillars[F, Config]): F[Unit]
