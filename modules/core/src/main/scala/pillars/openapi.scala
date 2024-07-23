package pillars

import cats.effect.IO
import cats.syntax.all.*
import com.monovore.decline.*
import fs2.Stream
import fs2.io.file.Files
import fs2.io.file.Path
import fs2.text
import java.net.URI
import sttp.apispec.openapi.circe.yaml.*
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter

object openapi:

    val command = Command("openapi", "Generate OpenAPI documentation"):
        val output                   = Opts.option[Path]("output", "Output file").orNone
        val servers: Opts[List[URI]] =
            Opts.options[URI]("server", "Server URL").map(_.toList).withDefault(Nil)
        (output, servers).mapN(Generator.Args.apply)
    final case class Generator(app: App[IO]):
        def generate(args: Generator.Args): IO[Unit] =
            val yaml = OpenAPIDocsInterpreter()
                .toOpenAPI(app.endpoints, app.infos.name, app.infos.version)
                .toYaml
            println(yaml)
            args.output match
                case Some(path) =>
                    Stream.emit(yaml)
                        .covary[IO]
                        .through(text.utf8.encode)
                        .through(Files[IO].writeAll(path))
                        .compile
                        .drain
                case None       => IO.println(yaml)
            end match
        end generate
    end Generator

    object Generator:
        final case class Args(output: Option[Path], servers: List[URI])
end openapi
