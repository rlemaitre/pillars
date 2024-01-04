package pillars

import cats.effect.Sync
import cats.effect.kernel
import cats.effect.kernel.Resource
import pillars.config.PillarConfig
import pillars.observability.Observability
import scribe.Scribe
import scribe.ScribeImpl
import skunk.Session

final case class Pillars[F[_]: Sync, Config](
    observability: Observability[F],
    config: PillarConfig[Config],
    pool: Resource[F, Session[F]]
):
  val logger: Scribe[F] = ScribeImpl(kernel.Sync[F])
