package pillars

import cats.effect.Sync
import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import sttp.tapir.Schema
import sttp.tapir.codec.iron.*
import sttp.tapir.codec.iron.given

package object flags:
    given Encoder[FeatureFlag.Status] = Encoder.encodeString.contramap:
        case FeatureFlag.Status.Enabled  => "enabled"
        case FeatureFlag.Status.Disabled => "disabled"

    given Decoder[FeatureFlag.Status] = Decoder.decodeString.emap:
        case "enabled"  => Right(FeatureFlag.Status.Enabled)
        case "disabled" => Right(FeatureFlag.Status.Disabled)
        case other      => Left(s"Invalid status $other")

    given Schema[FeatureFlag.Status] = Schema.derived

    given Codec[FeatureFlag] = Codec.AsObject.derived

    given Schema[FeatureFlag]     = Schema.derived
    extension[F[_]: Sync](pillars: Pillars[F])
        def flags: FlagManager[F] = pillars.module[FlagManager[F]]
        def whenEnabled[A](flag: FeatureFlag.Name)(thunk: => F[A]): F[Unit] =
            pillars.flags.when(flag)(thunk)
end flags
