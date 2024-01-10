package pillars.config

import org.http4s.Uri
import pillars.probes.Component
import scala.concurrent.duration.*

case class ProbesConfig(enabled: Boolean = true, probes: List[ProbeConfig] = List.empty)
enum ProbeConfig(
    val name: Component.Name,
    val timeout: FiniteDuration,
    val interval: FiniteDuration,
    val failureCount: Int
):
  case Database(
      override val name: Component.Name,
      override val timeout: FiniteDuration = 2.seconds,
      override val interval: FiniteDuration = 30.seconds,
      override val failureCount: Int = 3
  ) extends ProbeConfig(name, timeout, interval, failureCount)
  case Http(
      override val name: Component.Name,
      override val timeout: FiniteDuration = 2.seconds,
      override val interval: FiniteDuration = 30.seconds,
      override val failureCount: Int = 3,
      url: Uri
  ) extends ProbeConfig(name, timeout, interval, failureCount)

  def component: Component = this match
    case database: Database => Component(name, Component.Type.Datastore)
    case http: Http         => Component(name, Component.Type.Component)
