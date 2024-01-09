package example
import cats.Monad
import pillars.http.server.Controller.HttpEndpoint

object endpoints:
  def all[F[_]: Monad]: List[HttpEndpoint[F]] = List.empty
