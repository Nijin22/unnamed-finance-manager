package info.dennis_weber.unfima.api.handlers.v1_0

import com.google.inject.Inject
import groovy.sql.Sql
import info.dennis_weber.unfima.api.services.DatabaseService
import ratpack.groovy.handling.GroovyContext

/**
 * Takes care of authenticating a user and then delegates the rest of the handling to a concrete class with the
 * handleAuthenticated method.
 */
abstract class AbstractAuthenticatedUnfimaHandler extends AbstractUnfimaHandler {
  @Inject
  DatabaseService dbService

  @Override
  protected void handle(GroovyContext ctx) {
    String header
    String token

    // Extract token
    try {
      header = ctx.header("Authorization").get()
      token = header.split(" ")[1]
    } catch (Exception ignored) {
      errResp(ctx, 401, "Failed to extract Authorization token.", "NO_TOKEN_PROVIDED")
      return
    }

    // Verify token
    final String selectUserIdStatement = "SELECT userId FROM sessions WHERE token = ?"
    Integer userId = dbService.getGroovySql().firstRow(selectUserIdStatement, [token])?.get("userId") as Integer
    if (userId == null) {
      errResp(ctx, 401, "Token '$token' is invalid.", "TOKEN_NOT_VALID")
      return
    }

    // Update lastUsageTimestamp in token
    final String updateTimestampStatement = "UPDATE sessions SET lastUsageTimestamp = ? WHERE token = ?"
    long timestamp = (new Date().getTime() / 1000) as long
    dbService.groovySql.executeUpdate(updateTimestampStatement, [timestamp, token])

    handleAuthenticated(ctx, userId)
  }

  /**
   * What to do when the user was authenticated successfully
   * @param ctx
   * @param userId
   */
  abstract void handleAuthenticated(GroovyContext ctx, int userId)
}
