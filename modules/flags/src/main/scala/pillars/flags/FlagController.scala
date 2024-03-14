package pillars.flags

import cats.Functor
import cats.syntax.all.*
import io.github.iltotore.iron.*
import pillars.AdminServer.baseEndpoint
import pillars.Controller
import pillars.Controller.HttpEndpoint
import pillars.PillarsError
import pillars.PillarsError.Code
import pillars.PillarsError.ErrorNumber
import pillars.PillarsError.Message
import pillars.flags.FlagController.FlagEndpoints
import pillars.flags.FlagController.FlagError
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.codec.iron.given
import sttp.tapir.json.circe.jsonBody

final case class FlagController[F[_]: Functor](manager: FlagManager[F]) extends Controller[F]:
    private val listAll = FlagEndpoints.list.serverLogicSuccess(_ => manager.flags)
    private val getOne  =
        FlagEndpoints.get.serverLogic: name =>
            manager
                .getFlag(name)
                .map:
                    case Some(flag) => Right(flag)
                    case None       => FlagError.FlagNotFound(name).httpResponse
    private val modify  =
        FlagEndpoints.edit.serverLogic: (name, flag) =>
            manager
                .setStatus(name, flag.status)
                .map:
                    case Some(flag) => Right(flag)
                    case None       => FlagError.FlagNotFound(name).httpResponse

    override def endpoints: List[HttpEndpoint[F]] = List(listAll, getOne, modify)
end FlagController

object FlagController:
    enum FlagError(
        val number: PillarsError.ErrorNumber,
        override val status: StatusCode,
        val message: PillarsError.Message
    ) extends PillarsError:
        override def code: Code = Code("FLAG")

        case FlagNotFound(name: Flag)
            extends FlagError(ErrorNumber(1), StatusCode.NotFound, Message(s"Flag ${name}not found".assume))
    end FlagError

    object FlagEndpoints:
        private val prefix = baseEndpoint.in("flags")

        def list = prefix.get.out(jsonBody[List[FeatureFlag]])

        def get = prefix.get.in(path[Flag]("name")).out(jsonBody[FeatureFlag])

        def edit = prefix.put.in(path[Flag]("name")).in(jsonBody[FlagDetails]).out(jsonBody[FeatureFlag])
    end FlagEndpoints

end FlagController
