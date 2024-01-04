package pillars.config

import cats.effect.IO
import cats.effect.kernel.Resource
import cats.syntax.all.*
import io.circe.Decoder
import io.circe.yaml.parser
import java.nio.file.Path
import scala.io.Source
import scala.util.matching.Regex

object ConfigReader:
  private def matcher(regMatch: Regex.Match): String = sys.env
    .getOrElse(regMatch.group(1), throw ConfigError.MissingEnvironmentVariable(regMatch.group(1)))
  private val regex: Regex = """\$\{([^}]+)}""".r

  def readConfig[T: Decoder](path: Path): Resource[IO, PillarConfig[T]] =
    Resource
      .fromAutoCloseable(IO(Source.fromFile(path.toFile)))
      .map(_.getLines().mkString("\n"))
      .map(regex.replaceAllIn(_, matcher))
      .evalMap: c =>
        IO.fromEither(parser.parse(c).leftMap(ConfigError.ParsingError(_)).flatMap(_.as[PillarConfig[T]]))
