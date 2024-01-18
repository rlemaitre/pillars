package pillars.flags

import FeatureFlag.Name
import cats.effect.Async
import cats.effect.Ref
import cats.effect.Resource
import cats.effect.Sync
import cats.effect.std.Console
import cats.syntax.all.*
import fs2.io.net.Network
import org.typelevel.otel4s.trace.Tracer
import pillars.Controller
import pillars.Loader
import pillars.Module
import pillars.Pillars
import pillars.probes.Probe

trait FlagManager[F[_]: Sync] extends Module[F]:
    def isEnabled(flag: FeatureFlag.Name): F[Boolean]
    def getFlag(name: FeatureFlag.Name): F[Option[FeatureFlag]]
    def flags: F[List[FeatureFlag]]
    def when[A](flag: FeatureFlag.Name)(thunk: => F[A]): F[Unit] =
        isEnabled(flag).flatMap:
            case true  => thunk.void
            case false => Sync[F].unit

    extension (pillars: Pillars[F])
        def flags: FlagManager[F]                                    = this
        def when(flag: FeatureFlag.Name)(thunk: => F[Unit]): F[Unit] = this.when(flag)(thunk)
end FlagManager

object FlagManager:
    def noop[F[_]: Sync]: FlagManager[F] =
        new FlagManager[F]:
            override def isEnabled(flag: Name): F[Boolean]                       = false.pure[F]
            override def getFlag(name: FeatureFlag.Name): F[Option[FeatureFlag]] = None.pure[F]
            override def flags: F[List[FeatureFlag]]                             = List.empty.pure[F]
            override def adminControllers: List[Controller[F]]                   = Nil
            override def probes: List[Probe[F]]                                  = Nil
end FlagManager

class FlagManagerLoader extends Loader:
    override type M[F[_]] = FlagManager[F]
    def name: String = "feature-flags"

    def load[F[_]: Async: Network: Tracer: Console](context: Loader.Context[F]): Resource[F, FlagManager[F]] =
        import context.*
        Resource.eval:
            for
                _       <- logger.info("Loading DB config")
                config  <- configReader.read[FeatureFlagsConfig](name)
                manager <-
                    if !config.enabled then Sync[F].pure(FlagManager.noop[F])
                    else
                        val flags = config.flags.groupBy(_.name).map((name, flags) => name -> flags.head)
                        Ref
                            .of[F, Map[Name, FeatureFlag]](flags)
                            .map: ref =>
                                new FlagManager[F]:
                                    def flags: F[List[FeatureFlag]] = ref.get.map(_.values.toList)

                                    def getFlag(name: FeatureFlag.Name): F[Option[FeatureFlag]] =
                                        ref.get.map(_.get(name))

                                    def isEnabled(flag: FeatureFlag.Name): F[Boolean] =
                                        ref.get.map(_.get(flag).exists(_.isEnabled))

                                    override def adminControllers: List[Controller[F]] = FlagController(this).pure[List]
                    end if
            yield manager
    end load
end FlagManagerLoader
