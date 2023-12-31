package pillars.admin.controllers

import cats.Functor
import cats.syntax.all.*
import io.github.iltotore.iron.*
import pillars.PillarsError
import pillars.PillarsError.Code
import pillars.PillarsError.ErrorNumber
import pillars.PillarsError.Message
import pillars.admin.controllers.FlagController.FlagError
import pillars.admin.endpoints.flags.*
import pillars.admin.views.errorView
import pillars.flags.FlagManager
import pillars.http.server.Controller
import pillars.http.server.Controller.HttpEndpoint
import pillars.model.FeatureFlag
import sttp.model.StatusCode

final case class FlagController[F[_]: Functor](manager: FlagManager[F]) extends Controller[F]:
  private val listAll = list.serverLogicSuccess(_ => manager.flags)
  private val getOne =
    get.serverLogic: name =>
      manager
        .getFlag(name)
        .map:
          case Some(flag) => Right(flag)
          case None       => errorView(FlagError.FlagNotFound(name))

  override def endpoints: List[HttpEndpoint[F]] = List(listAll, getOne)

object FlagController:
  enum FlagError(
      val number: PillarsError.ErrorNumber,
      override val status: StatusCode,
      val message: PillarsError.Message
  ) extends PillarsError:
    override def code: Code = Code("FLAG")

    case FlagNotFound(name: FeatureFlag.Name)
        extends FlagError(ErrorNumber(1), StatusCode.NotFound, Message(s"Flag ${name}not found".assume))
