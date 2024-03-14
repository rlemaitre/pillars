package example

import io.github.iltotore.iron.*
import pillars.ApiServer
import pillars.PillarsError
import pillars.PillarsError.*
import sttp.model.StatusCode

object errors:
    enum api(val number: PillarsError.ErrorNumber, override val status: StatusCode, val message: PillarsError.Message)
        extends ApiServer.Error:
        case NotImplemented extends api(ErrorNumber(1), StatusCode.NotImplemented, Message("Not implemented"))
        case NotFound       extends api(ErrorNumber(2), StatusCode.NotFound, Message("Not found"))
        case AlreadyExists  extends api(ErrorNumber(3), StatusCode.Conflict, Message("Already exists"))
    end api
end errors
