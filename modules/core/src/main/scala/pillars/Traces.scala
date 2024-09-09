package pillars

import cats.effect.Async
import cats.syntax.all.*
import org.typelevel.otel4s.trace.Span
import org.typelevel.otel4s.trace.SpanKind
import org.typelevel.otel4s.trace.Tracer
import sttp.monad.MonadError
import sttp.tapir.Endpoint
import sttp.tapir.model.ServerRequest
import sttp.tapir.server.interceptor.DecodeFailureContext
import sttp.tapir.server.interceptor.DecodeSuccessContext
import sttp.tapir.server.interceptor.EndpointHandler
import sttp.tapir.server.interceptor.EndpointInterceptor
import sttp.tapir.server.interceptor.Responder
import sttp.tapir.server.interceptor.SecurityFailureContext
import sttp.tapir.server.interpreter.BodyListener
import sttp.tapir.server.model.ServerResponse

final case class Traces[F[_]: Async](tracer: Tracer[F]) extends EndpointInterceptor[F]:
    override def apply[B](responder: Responder[F, B], endpointHandler: EndpointHandler[F, B]): EndpointHandler[F, B] =
        new EndpointHandler[F, B]:
            override def onDecodeSuccess[A, U, I](
                ctx: DecodeSuccessContext[F, A, U, I]
            )(using monad: MonadError[F], bodyListener: BodyListener[F, B]): F[ServerResponse[B]] =
                trace(ctx.endpoint, ctx.request, endpointHandler.onDecodeSuccess(ctx))
            end onDecodeSuccess

            override def onDecodeFailure(ctx: DecodeFailureContext)(using
                monad: MonadError[F],
                bodyListener: BodyListener[F, B]
            ): F[Option[ServerResponse[B]]] =
                tracer
                    .spanBuilder(spanName(ctx.endpoint))
                    .withSpanKind(SpanKind.Server)
                    .addAttributes(Observability.Attributes.fromTapirRequest(ctx.request))
                    .addAttributes(Observability.Attributes.fromTapirEndpoint(ctx.endpoint))
                    .build
                    .use: span =>
                        for
                            _        <- span.addEvent("Send request")
                            response <- handle(span, endpointHandler.onDecodeFailure(ctx))
                            _        <- response.traverse_(r =>
                                            span.addAttributes(Observability.Attributes.fromTapirResponse(r))
                                        )
                            _        <- span.addEvent("Request received")
                        yield response

            override def onSecurityFailure[A](ctx: SecurityFailureContext[F, A])(using
                monad: MonadError[F],
                bodyListener: BodyListener[F, B]
            ): F[ServerResponse[B]] =
                trace(ctx.endpoint, ctx.request, endpointHandler.onSecurityFailure(ctx))

            private def trace[O, I, U, A](
                endpoint: Endpoint[A, I, ?, ?, ?],
                request: ServerRequest,
                execution: F[ServerResponse[O]]
            ): F[ServerResponse[O]] =
                tracer
                    .spanBuilder(spanName(endpoint))
                    .withSpanKind(SpanKind.Server)
                    .addAttributes(Observability.Attributes.fromTapirRequest(request))
                    .addAttributes(Observability.Attributes.fromTapirEndpoint(endpoint))
                    .build
                    .use: span =>
                        for
                            _        <- span.addEvent("Send request")
                            response <- handle(span, execution)
                            _        <- span.addAttributes(Observability.Attributes.fromTapirResponse(response))
                            _        <- span.addEvent("Request received")
                        yield response

            private def spanName(endpoint: Endpoint[?, ?, ?, ?, ?]): String =
                s"${endpoint.method.map(_.method).getOrElse("*")} ${endpoint.showPathTemplate(showQueryParam = None)}"

            private def handle[T](span: Span[F], execution: F[T]): F[T] =
                execution.onError: error =>
                    span.addEvent("Error") *>
                        span.addAttributes(Observability.Attributes.fromError(error))

end Traces
