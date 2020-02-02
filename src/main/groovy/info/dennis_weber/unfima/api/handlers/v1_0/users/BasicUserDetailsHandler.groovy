package info.dennis_weber.unfima.api.handlers.v1_0.users

import info.dennis_weber.unfima.api.handlers.v1_0.AbstractAuthenticatedUnfimaHandler
import ratpack.groovy.handling.GroovyContext

import static ratpack.jackson.Jackson.json

class BasicUserDetailsHandler extends AbstractAuthenticatedUnfimaHandler {
  @Override
  void handleAuthenticated(GroovyContext ctx, int userId) {
    final String getUserDetailsStatement = "SELECT email FROM users WHERE id = ?"

    String email = dbService.groovySql.firstRow(getUserDetailsStatement, [userId]).get("email")

    ctx.render(json(["id": userId, "email": email]))
  }
}
