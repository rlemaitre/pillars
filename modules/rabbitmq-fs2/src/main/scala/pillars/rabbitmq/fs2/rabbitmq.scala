// Copyright (c) 2024-2024 by Raphaël Lemaitre and Contributors
// This software is licensed under the Eclipse Public License v2.0 (EPL-2.0).
// For more information see LICENSE or https://opensource.org/license/epl-2-0

package pillars.rabbitmq.fs2

import cats.data.NonEmptyList
import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import cats.syntax.applicative.*
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import com.comcast.ip4s.host
import com.comcast.ip4s.port
import dev.profunktor.fs2rabbit.config.Fs2RabbitConfig
import dev.profunktor.fs2rabbit.config.Fs2RabbitNodeConfig
import dev.profunktor.fs2rabbit.interpreter.RabbitClient
import fs2.io.file.Files
import fs2.io.net.Network
import io.circe.Codec
import io.circe.derivation.Configuration
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import org.typelevel.otel4s.trace.Tracer
import pillars.Config.Secret
import pillars.Loader
import pillars.Module
import pillars.Modules
import pillars.Pillars
import pillars.codec.given
import pillars.probes.Component
import pillars.probes.Probe
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

def rabbit[F[_]](using p: Pillars[F]): RabbitMQ[F] = p.module[RabbitMQ[F]](RabbitMQ.Key)

final case class RabbitMQ[F[_]: Async](config: RabbitMQConfig, client: RabbitClient[F]) extends Module[F]:
    override type ModuleConfig = RabbitMQConfig
    export client.*

    override def probes: List[Probe[F]] =
        val probe = new Probe[F]:
            override def component: Component = Component(Component.Name("rabbitmq"), Component.Type.Datastore)
            override def check: F[Boolean]    = true.pure[F]
        probe.pure[List]
    end probes
end RabbitMQ

object RabbitMQ:
    case object Key extends Module.Key:
        override val name: String = "rabbitmq"

    def apply[F[_]](using p: Pillars[F]): RabbitMQ[F] = p.module[RabbitMQ[F]](RabbitMQ.Key)

    def apply[F[_]: Async](config: RabbitMQConfig): Resource[F, RabbitMQ[F]] =
        RabbitClient.default[F](config.convert).resource.map(apply(config, _))

end RabbitMQ

class RabbitMQLoader extends Loader:
    override type M[F[_]] = RabbitMQ[F]
    override val key: Module.Key = RabbitMQ.Key

    override def load[F[_]: Async: Network: Tracer: Console](
        context: Loader.Context[F],
        modules: Modules[F]
    ): Resource[F, RabbitMQ[F]] =
        import context.*
        given Files[F] = Files.forAsync[F]
        for
            _      <- Resource.eval(logger.info("Loading RabbitMQ module"))
            config <- Resource.eval(reader.read[RabbitMQConfig]("rabbitmq"))
            client <- RabbitMQ[F](config)
            _      <- Resource.eval(logger.info("RabbitMQ module loaded"))
        yield client
        end for
    end load
end RabbitMQLoader

case class RabbitMQConfig(
    nodes: NonEmptyList[RabbitMQConfig.Node] = NonEmptyList.one(RabbitMQConfig.Node(host"localhost", port"5672")),
    virtualHost: RabbitMQVirtualHost = RabbitMQVirtualHost("/"),
    connectionTimeout: FiniteDuration = 5 seconds,
    ssl: Boolean = true,
    username: Option[RabbitMQUser] = None,
    password: Option[Secret[RabbitMQPassword]] = None,
    requeueOnNack: Boolean = true,
    requeueOnReject: Boolean = true,
    internalQueueSize: Option[Int :| Positive] = Some(1024),
    requestedHeartbeat: FiniteDuration = 60 seconds,
    automaticRecovery: Boolean = true,
    automaticTopologyRecovery: Boolean = true,
    clientProvidedConnectionName: Option[RabbitMQConnectionName] = None
) extends pillars.Config

object RabbitMQConfig:
    final case class Node(host: Host, port: Port) extends pillars.Config derives Codec.AsObject
    given Configuration         = Configuration.default.withKebabCaseMemberNames.withKebabCaseConstructorNames.withDefaults
    given Codec[RabbitMQConfig] = Codec.AsObject.derivedConfigured

    given Conversion[RabbitMQConfig.Node, Fs2RabbitNodeConfig] =
        node => Fs2RabbitNodeConfig(node.host.toString, node.port.value)
    given Conversion[RabbitMQConfig, Fs2RabbitConfig]          = cfg =>
        Fs2RabbitConfig(
          nodes = cfg.nodes.map(_.convert),
          virtualHost = cfg.virtualHost,
          connectionTimeout = cfg.connectionTimeout,
          ssl = cfg.ssl,
          username = cfg.username,
          password = cfg.password.map(_.value),
          requeueOnNack = cfg.requeueOnNack,
          requeueOnReject = cfg.requeueOnReject,
          internalQueueSize = cfg.internalQueueSize,
          requestedHeartbeat = cfg.requestedHeartbeat,
          automaticRecovery = cfg.automaticRecovery,
          automaticTopologyRecovery = cfg.automaticTopologyRecovery,
          clientProvidedConnectionName = cfg.clientProvidedConnectionName
        )
end RabbitMQConfig

private type RabbitMQVirtualHostConstraint = Not[Blank] DescribedAs "RabbitMQ virtual host must not be blank"
opaque type RabbitMQVirtualHost <: String  = String :| RabbitMQVirtualHostConstraint
object RabbitMQVirtualHost extends RefinedTypeOps[String, RabbitMQVirtualHostConstraint, RabbitMQVirtualHost]

private type RabbitMQUserConstraint = Not[Blank] DescribedAs "RabbitMQ user must not be blank"
opaque type RabbitMQUser <: String  = String :| RabbitMQUserConstraint
object RabbitMQUser extends RefinedTypeOps[String, RabbitMQUserConstraint, RabbitMQUser]

private type RabbitMQPasswordConstraint = Not[Blank] DescribedAs "RabbitMQ password must not be blank"
opaque type RabbitMQPassword <: String  = String :| RabbitMQPasswordConstraint
object RabbitMQPassword extends RefinedTypeOps[String, RabbitMQPasswordConstraint, RabbitMQPassword]

private type RabbitMQConnectionNameConstraint = Not[Blank] DescribedAs "RabbitMQ connection name must not be blank"
opaque type RabbitMQConnectionName <: String  = String :| RabbitMQConnectionNameConstraint
object RabbitMQConnectionName extends RefinedTypeOps[String, RabbitMQConnectionNameConstraint, RabbitMQConnectionName]
