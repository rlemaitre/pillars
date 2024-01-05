package pillars

import io.circe.{Codec, Encoder}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pillars.PillarsError.*
import sttp.tapir.Schema

import scala.util.control.NoStackTrace

trait PillarsError extends Throwable, NoStackTrace:
  def code: Code
  def number: ErrorNumber
  def message: Message
  override def getMessage: String = f"$code-$number%04d : $message"
  final def view: View = View(f"$code-$number%04d", message)

object PillarsError:
  private type CodeConstraint = (Not[Empty] & LettersUpperCase) DescribedAs "Code cannot be empty"
  opaque type Code <: String  = String :| CodeConstraint
  object Code extends RefinedTypeOps[String, CodeConstraint, Code]

  private type MessageConstraint = Not[Empty] DescribedAs "Message cannot be empty"
  opaque type Message <: String  = String :| MessageConstraint
  object Message extends RefinedTypeOps[String, MessageConstraint, Message]

  private type ErrorNumberConstraint = Positive DescribedAs "Number must be strictly positive"
  opaque type ErrorNumber <: Int  = Int :| ErrorNumberConstraint
  object ErrorNumber extends RefinedTypeOps[Int, ErrorNumberConstraint, ErrorNumber]
  
  final case class View(code: String, message: String) derives Codec.AsObject
  object View:
    given Schema[View] = Schema.derived[View]
