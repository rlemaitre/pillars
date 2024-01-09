package pillars.admin.controllers

import cats.Applicative
import cats.syntax.all.*
import pillars.admin.endpoints.probes.*
import pillars.http.server.Controller
import pillars.http.server.Controller.HttpEndpoint

final case class ProbesController[F[_]: Applicative]() extends Controller[F]:
  private val alive                             = liveness.serverLogicSuccess(_ => "OK".pure[F])
  private val ready                             = readiness.serverLogicSuccess(_ => "OK".pure[F])
  override val endpoints: List[HttpEndpoint[F]] = List(alive, ready)

object ProbesController:
  def apply[F[_]: Applicative](): ProbesController[F] = new ProbesController[F]()
