package pillars

import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import org.typelevel.literally.Literally
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

    given Schema[FeatureFlag] = Schema.derived
    extension [F[_]](p: Pillars[F])
        def flags: FlagManager[F]                                           = p.module[FlagManager[F]]
        def whenEnabled[A](flag: FeatureFlag.Name)(thunk: => F[A]): F[Unit] =
            p.module[FlagManager[F]].when(flag)(thunk)

    extension (inline ctx: StringContext)
        inline def flag(inline args: Any*): FeatureFlag.Name =
            ${ FlagLiteral('ctx, 'args) }
    object FlagLiteral extends Literally[FeatureFlag.Name]:
        override inline def validate(s: String)(using Quotes) =
            if FeatureFlag.Name.rtc.test(s) then Right('{ FeatureFlag.Name.applyUnsafe(${ Expr(s) }) })
            else Left(FeatureFlag.Name.rtc.message)

    extension (flag: FeatureFlag.Name)
        def whenEnabled[F[_], A](using p: Pillars[F])(thunk: => F[A]): F[Unit] =
            p.module[FlagManager[F]].when(flag)(thunk)
end flags
