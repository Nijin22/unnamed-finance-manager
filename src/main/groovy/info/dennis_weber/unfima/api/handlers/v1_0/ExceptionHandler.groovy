package info.dennis_weber.unfima.api.handlers.v1_0

import ratpack.error.ServerErrorHandler
import ratpack.handling.Context

import static ratpack.jackson.Jackson.json

class ExceptionHandler implements ServerErrorHandler {
  @Override
  void error(Context ctx, Throwable e) throws Exception {
    e.printStackTrace()

    ctx.response.status(500)
    ctx.response.contentType("application/json")
    Map response = [
        "errorClass"  : e.class,
        "errorMessage": e.message,
    ]
    ctx.render(json(response))
  }
}
