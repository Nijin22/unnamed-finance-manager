package info.dennis_weber.unfima.api.handlers.v1_0

import info.dennis_weber.unfima.api.errors.AbstractUserException
import ratpack.error.ServerErrorHandler
import ratpack.handling.Context

import static ratpack.jackson.Jackson.json

class ExceptionHandler implements ServerErrorHandler {
  @Override
  void error(Context ctx, Throwable e) throws Exception {
    if (e instanceof AbstractUserException) {
      handleUserError(ctx, e)
      return
    }

    handleUnexpectedError(ctx, e)
  }

  private static void handleUserError(Context ctx, AbstractUserException e) {
    ctx.response.status(e.statusCode)
    ctx.response.contentType("application/json")
    Map response = [
        "errorMsg": e.errorMsg,
        "errorId" : e.errorId,
    ]
    ctx.render(json(response))
  }

  private static void handleUnexpectedError(Context ctx, Throwable e) {
    // might be helpful, if the IDE is open:
    e.printStackTrace()

    // send a general error code
    ctx.response.status(500)
    ctx.response.contentType("application/json")
    Map response = [
        "info"      : "A unexpected error occurred. Ideally, this should never happen. " +
            "If you'd like to help us fix this, please report this error and the steps you did to: " +
            "https://github.com/Nijin22/unnamed-finance-manager/issues",
        "errorClass": e.class,
        "errorMsg"  : e.message,
        "url"       : ctx.request.uri,
        "stackTrace": [e.stackTrace.collect({ element ->
          return element.toString()
        })]
    ]
    ctx.render(json(response))
  }
}
