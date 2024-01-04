package pillars.logging

import cats.effect.IO
import cats.syntax.all.*
import io.circe.*
import io.circe.syntax.*
import java.nio.file.Path
import pillars.config.LogConfig
import scribe.Level
import scribe.Logger

object Log:
  def init(config: LogConfig): IO[Logger] =
    IO(
      Logger.root
        .clearHandlers()
        .withHandler(
          minimumLevel = Some(config.level)
//        writer = ScribeCirceJsonSupport.writer
        )
        .replace()
    )

  enum Format:
    case Json
    case Simple
    case Colored
    case Classic
    case Compact
    case Enhanced
    case Advanced
    case Strict
  object Format:
    given Encoder[Format] = Encoder.encodeString.contramap(_.toString.toLowerCase)
    given Decoder[Format] = Decoder.decodeString.emap {
      case "json"     => Right(Format.Json)
      case "simple"   => Right(Format.Simple)
      case "colored"  => Right(Format.Colored)
      case "classic"  => Right(Format.Classic)
      case "compact"  => Right(Format.Compact)
      case "enhanced" => Right(Format.Enhanced)
      case "advanced" => Right(Format.Advanced)
      case "strict"   => Right(Format.Strict)
      case other      => Left(s"Unknown output format: $other")
    }
  enum Output:
    case Console
    case File(path: Path)

  object Output:
    given Encoder[Output] = Encoder.instance:
      case Output.File(path) => Json.obj("type" -> "file".asJson, "path" -> path.toString.asJson)
      case Output.Console    => Json.obj("type" -> "console".asJson)
    given Decoder[Output] = Decoder.instance: cursor =>
      cursor
        .downField("type")
        .as[String]
        .flatMap:
          case "console" => Right(Output.Console)
          case "file" =>
            for
              p <- cursor.downField("path").as[String]
              path <- Either.cond(
                p.nonEmpty,
                Path.of(p),
                DecodingFailure("Missing path for file output", cursor.history)
              )
            yield Output.File(path)
          case other => Left(DecodingFailure(s"Unknown output type: $other", cursor.history))
