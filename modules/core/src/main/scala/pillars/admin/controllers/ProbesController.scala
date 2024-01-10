package pillars.admin.controllers

import cats.Applicative
import cats.syntax.all.*
import pillars.admin.endpoints.probes.*
import pillars.admin.views.CheckStatus
import pillars.admin.views.HealthStatus
import pillars.http.server.Controller
import pillars.http.server.Controller.HttpEndpoint
import pillars.probes.ProbeManager
import pillars.probes.Status

final case class ProbesController[F[_]: Applicative](manager: ProbeManager[F]) extends Controller[F]:
  private val alive = liveness.serverLogicSuccess(_ => "OK".pure[F])
  private val ready =
    readiness.serverLogicSuccess: _ =>
      manager.status.map: statuses =>
        val checks = statuses.map: (component, status) =>
          CheckStatus(component.name, component.`type`, status)
        val globalStatus = statuses.values.foldLeft(Status.pass)(_ |+| _)
        HealthStatus(globalStatus, checks.toList)
  override val endpoints: List[HttpEndpoint[F]] = List(alive, ready)
