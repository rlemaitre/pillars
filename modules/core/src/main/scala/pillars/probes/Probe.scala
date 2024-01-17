package pillars.probes

import pillars.config.ProbeConfig

trait Probe[F[_]]:
    def component: Component
    def check: F[Boolean]
    def config: ProbeConfig = ProbeConfig()
end Probe
