package info.dennis_weber.unfima.api.handlers.v1_0

import ratpack.groovy.handling.GroovyHandler
import ratpack.handling.Context

abstract class AbstractUnfimaHandler extends GroovyHandler {

  /**
   * Helper method to quickly generate a error response in the context
   *
   * @param ctx context to write to
   * @param status HTTP code (e.g. 400)
   * @param errorMsg text to show in response JSON
   */
  static void errResp(Context ctx, int status, String errorMsg) {
    ctx.response.status(status)
    ctx.response.contentType("application/json")
    Map<String, String> res = ["errorMsg": errorMsg]
    ctx.render(res)
  }

}
