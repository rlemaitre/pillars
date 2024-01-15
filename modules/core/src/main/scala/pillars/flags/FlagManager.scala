package pillars.flags

import cats.effect.Ref
import cats.effect.Sync
import cats.syntax.all.*
import pillars.config.FeatureFlagsConfig
import pillars.model.FeatureFlag
import pillars.model.FeatureFlag.Name

trait FlagManager[F[_]]:
    def isEnabled(flag: FeatureFlag.Name): F[Boolean]
    def getFlag(name: FeatureFlag.Name): F[Option[FeatureFlag]]
    def flags: F[List[FeatureFlag]]
end FlagManager

object FlagManager:
    def init[F[_]: Sync](config: FeatureFlagsConfig): F[FlagManager[F]] =
        if !config.enabled then
            Sync[F].delay:
                new FlagManager[F]:
                    override def isEnabled(flag: Name): F[Boolean]                       = false.pure[F]
                    override def getFlag(name: FeatureFlag.Name): F[Option[FeatureFlag]] = None.pure[F]
                    override def flags: F[List[FeatureFlag]]                             = List.empty.pure[F]
        else
            val flags = config.flags.groupBy(_.name).map((name, flags) => name -> flags.head)
            println(s"Feature flags: $flags")
            Ref
                .of[F, Map[Name, FeatureFlag]](flags)
                .map: ref =>
                    new FlagManager[F]:
                        def flags: F[List[FeatureFlag]]                             = ref.get.map(_.values.toList)
                        def getFlag(name: FeatureFlag.Name): F[Option[FeatureFlag]] = ref.get.map(_.get(name))
                        def isEnabled(flag: FeatureFlag.Name): F[Boolean]           = ref.get.map(_.get(flag).exists(_.isEnabled))
end FlagManager
