package pillars.flags

import io.circe.Codec
import sttp.tapir.Schema

final case class FlagDetails(status: Status) derives Codec.AsObject, Schema
