package pillars

import cats.effect.kernel.Resource
import pillars.config.PillarConfig
import pillars.observability.Observability
import skunk.Session

final case class Pillars[F[_], Config](
    observability: Observability[F],
    config: PillarConfig[Config],
    pool: Resource[F, Session[F]]
)
