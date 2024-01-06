package example
import cats.Monad
import pillars.http.HttpEndpoint

object endpoints:
  def all[F[_]: Monad]: List[HttpEndpoint[F]] = pillars.admin.endpoints.all
