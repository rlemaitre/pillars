package pillars.db

import cats.effect.*
import cats.effect.std.Console
import cats.syntax.all.*
import fs2.io.net.Network
import io.github.iltotore.iron.*
import org.typelevel.otel4s.trace.Tracer
import pillars.Loader
import pillars.Module
import pillars.Pillars
import pillars.http.server.Controller
import pillars.probes.Component
import pillars.probes.Component.Name
import pillars.probes.Component.Type
import pillars.probes.Probe
import skunk.*
import skunk.codec.all.*
import skunk.implicits.sql

final case class DB[F[_]: Async: Network: Tracer: Console](pool: Resource[F, Session[F]]) extends Module[F]:
    export pool.*

    override def probes: List[Probe[F]] =
        List:
            new Probe[F]:
                override def component: Component = Component(Name("db"), Type.Datastore)
                override def check: F[Boolean]    = pool.use(session => session.unique(sql"select true".query(bool)))

    override def adminControllers: List[Controller[F]] = Nil

    extension (pillars: Pillars[F])
        def db: DB[F] = this
end DB

class DBLoader extends Loader:
    override type M[F[_]] = DB[F]
    override def name: String = "db"

    def load[F[_]: Async: Network: Tracer: Console](context: Loader.Context[F]): Resource[F, DB[F]] =
        import context.*
        for
            _       <- Resource.eval(logger.info("Loading DB config"))
            config  <- Resource.eval(configReader.read[DatabaseConfig]("db"))
            poolRes <- Session.pooled[F](
                         host = config.host.toString,
                         port = config.port.value,
                         database = config.database,
                         user = config.username,
                         password = config.password.some.map(_.value),
                         max = config.poolSize,
                         debug = config.debug
                       )
        yield DB(poolRes)
        end for
    end load
end DBLoader
