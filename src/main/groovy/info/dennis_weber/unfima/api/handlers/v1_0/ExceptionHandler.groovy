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
        "errorClass"  : e.class,
        "errorMessage": e.message,
    ]
    ctx.render(json(response))
  }
}
