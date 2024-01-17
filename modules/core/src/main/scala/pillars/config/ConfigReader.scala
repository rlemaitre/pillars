package pillars.config

import cats.effect.Sync
import cats.effect.kernel.Resource
import cats.syntax.all.*
import io.circe.Decoder
import io.circe.Json
import io.circe.ParsingFailure
import io.circe.yaml.Parser
import java.nio.file.Path
import scala.io.Source
import scala.util.matching.Regex

case class ConfigReader[F[_]](path: Path)(using Sync[F]):
    private def matcher(regMatch: Regex.Match): String = sys.env
        .getOrElse(regMatch.group(1), throw ConfigError.MissingEnvironmentVariable(regMatch.group(1)))
    private val regex: Regex                           = """\$\{([^}]+)}""".r

    def read[T: Decoder]: F[T]              =
        Resource
            .fromAutoCloseable(Sync[F].delay(Source.fromFile(path.toFile)))
            .map(_.getLines().mkString("\n"))
            .map(regex.replaceAllIn(_, matcher))
            .use: input =>
                Sync[F].fromEither:
                    Parser.default
                        .parse(input)
                        .leftMap: failure =>
                            ConfigError.ParsingError(failure)
                        .flatMap(_.as[T])
    def read[T: Decoder](key: String): F[T] =
        Resource
            .fromAutoCloseable(Sync[F].delay(Source.fromFile(path.toFile)))
            .map(_.getLines().mkString("\n"))
            .map(regex.replaceAllIn(_, matcher))
            .use: input =>
                Sync[F].fromEither:
                    Parser.default.parse(input) match
                    case Left(failure) => Left(ConfigError.ParsingError(failure))
                    case Right(json)   => json.hcursor.downField(key).as[T].leftMap(ConfigError.ParsingError.apply)
end ConfigReader
