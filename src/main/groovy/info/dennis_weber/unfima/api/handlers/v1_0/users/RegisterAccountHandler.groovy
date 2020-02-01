package info.dennis_weber.unfima.api.handlers.v1_0.users

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractUnfimaHandler
import info.dennis_weber.unfima.api.services.DatabaseService
import org.mindrot.jbcrypt.BCrypt
import ratpack.groovy.handling.GroovyContext

import java.sql.SQLIntegrityConstraintViolationException

class RegisterAccountHandler extends AbstractUnfimaHandler {
  private DatabaseService dbService

  RegisterAccountHandler(DatabaseService dbService) {
    this.dbService = dbService
  }

  @Override
  protected void handle(GroovyContext ctx) {
    final int MAX_EMAIL_LENGTH = 255
    final int MAX_PASSWORD_LENGTH = 100

    ctx.parse(RequestBody.class).then({
      body ->
        // verify email is set and valid
        if (body.email == null) {
          errResp(ctx, 400, "required parameter 'email' is missing")
          return
        }
        if (body.email.length() > MAX_EMAIL_LENGTH) {
          errResp(ctx, 400, "'email' parameter is too long.")
          return
        }

        // Verify PW is set and valid
        if (body.password == null) {
          errResp(ctx, 400, "required parameter 'password' is missing")
          return
        }
        if (body.password.length() > MAX_PASSWORD_LENGTH) {
          errResp(ctx, 400, "'password' parameter is too long")
          return
        }

        // Convert password in a format save for storage
        String hashedPassword = BCrypt.hashpw(body.password, BCrypt.gensalt())

        // Check if user is already in db
        String selectStatement = "SELECT COUNT(*) as count FROM `users` WHERE `email` = ?"
        GroovyRowResult row = dbService.getGroovySql().firstRow(selectStatement, [body.email])
        if (row.get("count") > 0) {
          errResp(ctx, 409, "email address is already in use")
          return
        }

        // Store new users
        Sql sql = dbService.getGroovySql()
        String insertStatement = "INSERT INTO `users` (email, password) VALUES (?, ?)"
        try {
          sql.executeInsert(insertStatement, [body.email, hashedPassword])
        } catch (SQLIntegrityConstraintViolationException e) {
          if (e.message.contains("'unique_emails'")) {
            // Email already in use. Immediately after checking for that. Geez, talk about bad luck...
            errResp(ctx, 409, "email address is already in use")
            return
          } else {
            // Unless we add another constraint, this shouldn't really happen
            throw e
          }
        }

        ctx.response.status(201)
        ctx.response.send() // No content
    })
  }

  static private class RequestBody {
    String email
    String password
  }
}
