package pillars

import pillars.http.server.Controller
import pillars.probes.Probe

trait Module[F[_]]:
    def probes: List[Probe[F]]
    def adminControllers: List[Controller[F]]
