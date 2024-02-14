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
    given Encoder[Status] = Encoder.encodeString.contramap:
        case Status.Enabled  => "enabled"
        case Status.Disabled => "disabled"

    given Decoder[Status] = Decoder.decodeString.emap:
        case "enabled"  => Right(Status.Enabled)
        case "disabled" => Right(Status.Disabled)
        case other      => Left(s"Invalid status $other")

    given Schema[Status] = Schema.derived

    given Codec[FeatureFlag] = Codec.AsObject.derived

    given Schema[FeatureFlag] = Schema.derived
    extension [F[_]](p: Pillars[F])
        def flags: FlagManager[F]                               = p.module(FlagManager.Key)

        def whenEnabled[A](flag: Flag)(thunk: => F[A]): F[Unit] =
            p.module[FlagManager[F]](FlagManager.Key).when(flag)(thunk)
    end extension

    extension (inline ctx: StringContext)
        inline def flag(inline args: Any*): Flag = ${ FlagLiteral('ctx, 'args) }
    object FlagLiteral extends Literally[Flag]:
        override def validate(s: String)(using Quotes): Either[String, Expr[Flag]] =
            if Flag.rtc.test(s) then Right('{ Flag.applyUnsafe(${ Expr(s) }) })
            else Left(Flag.rtc.message)

    extension (flag: Flag)
        def whenEnabled[F[_], A](using p: Pillars[F])(thunk: => F[A]): F[Unit] =
            p.module[FlagManager[F]](FlagManager.Key).when(flag)(thunk)
end flags
