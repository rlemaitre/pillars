package pillars

package object db:
    extension [F[_]] (pillars: Pillars[F])
        def db: DB[F] = pillars.module[DB[F]]
