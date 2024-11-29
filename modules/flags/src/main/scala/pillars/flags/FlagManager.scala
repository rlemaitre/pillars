// Copyright (c) 2024-2024 by Raphaël Lemaitre and Contributors
// This software is licensed under the Eclipse Public License v2.0 (EPL-2.0).
// For more information see LICENSE or https://opensource.org/license/epl-2-0

package pillars.flags

import cats.effect.Async
import cats.effect.Ref
import cats.effect.Resource
import cats.effect.Sync
import cats.effect.std.Console
import cats.syntax.all.*
import fs2.io.file.Files
import fs2.io.net.Network
import org.typelevel.otel4s.trace.Tracer
import pillars.Controller
import pillars.Module
import pillars.ModuleDef
import pillars.Modules
import pillars.Pillars

trait FlagManager[F[_]: Sync] extends Module[F]:
    override type ModuleConfig = FlagsConfig
    def isEnabled(flag: Flag): F[Boolean]
    def config: FlagsConfig
    def getFlag(name: Flag): F[Option[FeatureFlag]]
    def flags: F[List[FeatureFlag]]

    private[flags] def setStatus(flag: Flag, status: Status): F[Option[FeatureFlag]]
    def when[A](flag: Flag)(thunk: => F[A]): F[Unit] =
        isEnabled(flag).flatMap:
            case true  => thunk.void
            case false => Sync[F].unit

    extension (pillars: Pillars[F])
        def flags: FlagManager[F]                        = this
        def when(flag: Flag)(thunk: => F[Unit]): F[Unit] = this.when(flag)(thunk)
    end extension
end FlagManager

object FlagManager:
    case object Key extends Module.Key:
        def name: String = "feature-flags"
    end Key
    def noop[F[_]: Sync](conf: FlagsConfig): FlagManager[F] =
        new FlagManager[F]:
            def isEnabled(flag: Flag): F[Boolean]                    = false.pure[F]
            override def config: FlagsConfig                         = conf
            def getFlag(name: Flag): F[Option[FeatureFlag]]          = None.pure[F]
            def flags: F[List[FeatureFlag]]                          = List.empty.pure[F]
            private[flags] def setStatus(flag: Flag, status: Status) = None.pure[F]
end FlagManager

object FeatureFlagsModule extends ModuleDef:
    override type M[F[_]] = FlagManager[F]

    override def key: Module.Key = FlagManager.Key

    def load[F[_]: Async: Network: Tracer: Console](
        context: ModuleDef.Context[F],
        modules: Modules[F]
    ): Resource[F, FlagManager[F]] =
        import context.*
        given Files[F] = Files.forAsync[F]
        Resource.eval:
            for
                _       <- logger.info("Loading Feature flags module")
                config  <- reader.read[FlagsConfig](key.name)
                manager <- createManager(config)
                _       <- logger.info("Feature flags module loaded")
            yield manager
    end load

    private[flags] def createManager[F[_]: Async: Network: Tracer: Console](conf: FlagsConfig): F[FlagManager[F]] =
        if !conf.enabled then Sync[F].pure(FlagManager.noop[F](conf))
        else
            val flags = conf.flags.groupBy(_.name).map((name, flags) => name -> flags.head)
            Ref
                .of[F, Map[Flag, FeatureFlag]](flags)
                .map: ref =>
                    new FlagManager[F]:
                        def flags: F[List[FeatureFlag]] = ref.get.map(_.values.toList)

                        override def config: FlagsConfig = conf

                        def getFlag(name: Flag): F[Option[FeatureFlag]] =
                            ref.get.map(_.get(name))

                        def isEnabled(flag: Flag): F[Boolean] =
                            ref.get.map(_.get(flag).exists(_.isEnabled))

                        private[flags] def setStatus(flag: Flag, status: Status) =
                            ref
                                .updateAndGet: flags =>
                                    flags.updatedWith(flag):
                                        case Some(f) => Some(f.copy(status = status))
                                        case None    => None
                                .map(_.get(flag))

                        override def adminControllers: List[Controller[F]] = flagController(this).pure[List]
        end if
    end createManager
end FeatureFlagsModule
