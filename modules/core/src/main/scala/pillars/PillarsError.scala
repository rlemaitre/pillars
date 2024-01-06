package pillars

import io.circe.Codec
import io.circe.Encoder
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.PillarsError.*
import scala.util.control.NoStackTrace
import sttp.model.StatusCode
import sttp.tapir.Schema

trait PillarsError extends Throwable, NoStackTrace:
  def code: Code
  def number: ErrorNumber
  def message: Message
  def details: Option[String]     = None
  def status: StatusCode          = StatusCode.InternalServerError
  override def getMessage: String = f"$code-$number%04d : $message"
  final def view: View            = View(f"$code-$number%04d", message, details)

object PillarsError:
  def fromThrowable(throwable: Throwable): PillarsError = throwable match
    case error: PillarsError => error
    case _                   => Unknown(throwable)
  final case class Unknown(reason: Throwable) extends PillarsError:
    override def code: Code              = Code("ERR")
    override def number: ErrorNumber     = ErrorNumber(9999)
    override def message: Message        = Message("Internal server error")
    override def status: StatusCode      = StatusCode.InternalServerError
    override def details: Option[String] = Some(reason.getMessage)

  final case class View(code: String, message: String, details: Option[String]) derives Codec.AsObject
  object View:
    given Schema[View] = Schema.derived[View]

  private type CodeConstraint = (Not[Empty] & LettersUpperCase) DescribedAs "Code cannot be empty"
  opaque type Code <: String  = String :| CodeConstraint
  object Code extends RefinedTypeOps[String, CodeConstraint, Code]

  private type MessageConstraint = Not[Empty] DescribedAs "Message cannot be empty"
  opaque type Message <: String  = String :| MessageConstraint
  object Message extends RefinedTypeOps[String, MessageConstraint, Message]

  private type ErrorNumberConstraint = Positive DescribedAs "Number must be strictly positive"
  opaque type ErrorNumber <: Int     = Int :| ErrorNumberConstraint
  object ErrorNumber extends RefinedTypeOps[Int, ErrorNumberConstraint, ErrorNumber]
