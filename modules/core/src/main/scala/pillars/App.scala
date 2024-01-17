package pillars

import pillars.model.AppName
import pillars.model.Description
import pillars.model.Version
import pillars.probes.Probe

trait App[F[_]]:
    def name: AppName
    def version: Version
    def description: Description
    def probes: List[Probe[F]] = Nil
    def run(pillars: Pillars[F]): F[Unit]
end App
