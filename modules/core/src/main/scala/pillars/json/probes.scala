package pillars.json

import io.circe.Decoder
import io.circe.Encoder
import pillars.probes.Component
import pillars.probes.Status
import sttp.tapir.Schema

object probes:
    given Schema[Status]  = Schema.derived
    given Encoder[Status] = Encoder.encodeString.contramap(_.toString.toLowerCase)
    given Decoder[Status] = Decoder.decodeString.emap: status =>
        status.toLowerCase match
        case "pass" => Right(Status.pass)
        case "warn" => Right(Status.warn)
        case "fail" => Right(Status.fail)
        case other  => Left(s"Unknown status: $other")

    given Schema[Component.Type]  = Schema.derived
    given Encoder[Component.Type] = Encoder.encodeString.contramap(_.toString.toLowerCase)
    given Decoder[Component.Type] = Decoder.decodeString.emap: status =>
        status.toLowerCase match
        case "system"    => Right(Component.Type.System)
        case "datastore" => Right(Component.Type.Datastore)
        case "component" => Right(Component.Type.Component)
        case other       => Left(s"Unknown component type: $other")
end probes
