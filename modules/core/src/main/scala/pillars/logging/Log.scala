package pillars.logging

import cats.effect.IO
import io.circe.*
import io.circe.syntax.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import java.nio.file.Path
import pillars.config.LogConfig
import scribe.Level
import scribe.Logger
import scribe.file.PathBuilder
import scribe.format.Formatter
import scribe.json.ScribeCirceJsonSupport
import scribe.writer.ConsoleWriter
import scribe.writer.Writer

object Log:
  def init(config: LogConfig): IO[Unit] =
    IO(
      Logger.root
        .clearHandlers()
        .clearModifiers()
        .withHandler(
          formatter = config.format.formatter,
          minimumLevel = Some(config.level),
          writer = writer(config)
        )
        .replace()
    ).void

  private def writer(config: LogConfig): Writer =
    config.format match
      case Format.Json => ScribeCirceJsonSupport.writer(config.output.writer)
      case _           => config.output.writer

  private type BufferSizeConstraint = Positive DescribedAs "Buffer size should be positive"
  opaque type BufferSize <: Int     = Int :| BufferSizeConstraint
  object BufferSize extends RefinedTypeOps[Int, BufferSizeConstraint, BufferSize]

  enum Format:
    case Json
    case Simple
    case Colored
    case Classic
    case Compact
    case Enhanced
    case Advanced
    case Strict
    def formatter: Formatter = this match
      case Format.Json     => Formatter.default
      case Format.Simple   => Formatter.simple
      case Format.Colored  => Formatter.colored
      case Format.Classic  => Formatter.classic
      case Format.Compact  => Formatter.compact
      case Format.Enhanced => Formatter.enhanced
      case Format.Advanced => Formatter.advanced
      case Format.Strict   => Formatter.strict
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

    def writer: Writer = this match
      case Output.Console    => ConsoleWriter
      case Output.File(path) => scribe.file.FileWriter(PathBuilder.static(path))

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
