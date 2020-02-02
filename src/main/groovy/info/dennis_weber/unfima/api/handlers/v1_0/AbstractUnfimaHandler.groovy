package info.dennis_weber.unfima.api.handlers.v1_0

import ratpack.groovy.handling.GroovyHandler
import ratpack.handling.Context

import static ratpack.jackson.Jackson.json

abstract class AbstractUnfimaHandler extends GroovyHandler {

  /**
   * Helper method to quickly generate a error response in the context
   *
   * @param ctx context to write to
   * @param status HTTP code (e.g. 400)
   * @param errorMsg error description
   * @param errorId (optional) error ID suitable for parsing
   */
  static void errResp(Context ctx, int status, String errorMsg, String errorId = null) {
    ctx.response.status(status)
    ctx.response.contentType("application/json")
    Map<String, String> response = ["errorMsg": errorMsg]
    if (errorId != null) {
      response.put("errorId", errorId)
    }

    ctx.render(json(response))
  }

}
