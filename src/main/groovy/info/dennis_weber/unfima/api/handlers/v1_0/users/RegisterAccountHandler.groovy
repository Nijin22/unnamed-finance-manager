package info.dennis_weber.unfima.api.handlers.v1_0.users

import com.google.inject.Inject
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import info.dennis_weber.unfima.api.errors.BadFormatException
import info.dennis_weber.unfima.api.errors.ConflictException
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractUnfimaHandler
import info.dennis_weber.unfima.api.services.DatabaseService
import org.mindrot.jbcrypt.BCrypt
import ratpack.groovy.handling.GroovyContext

import java.sql.SQLIntegrityConstraintViolationException

class RegisterAccountHandler extends AbstractUnfimaHandler {

  @Inject
  DatabaseService dbService

  @Override
  protected void handle(GroovyContext ctx) {
    final int MAX_EMAIL_LENGTH = 255
    final int MAX_PASSWORD_LENGTH = 100

    ctx.parse(RequestBody.class).then({
      body ->
        // verify email is set and valid
        if (body.email == null) {
          throw new BadFormatException("required parameter 'email' is missing", null)
        }
        if (body.email.length() > MAX_EMAIL_LENGTH) {
          throw new BadFormatException("'email' parameter is too long.", null)
        }

        // Verify PW is set and valid
        if (body.password == null) {
          throw new BadFormatException("required parameter 'password' is missing", null)
        }
        if (body.password.length() > MAX_PASSWORD_LENGTH) {
          throw new BadFormatException("'password' parameter is too long", null)
        }

        // Convert password in a format save for storage
        String hashedPassword = BCrypt.hashpw(body.password, BCrypt.gensalt())

        // Check if user is already in db
        String selectStatement = "SELECT COUNT(*) as count FROM `users` WHERE `email` = ?"
        GroovyRowResult row = dbService.getGroovySql().firstRow(selectStatement, [body.email])
        if (row.get("count") > 0) {
          throw new ConflictException("email address is already in use", null)
        }

        // Store new users
        Sql sql = dbService.getGroovySql()
        String insertStatement = "INSERT INTO `users` (email, password) VALUES (?, ?)"
        try {
          sql.executeInsert(insertStatement, [body.email, hashedPassword])
        } catch (SQLIntegrityConstraintViolationException e) {
          if (e.message.contains("'unique_emails'")) {
            // Email already in use. Immediately after checking for that. Geez, talk about bad luck...
            throw new ConflictException("email address is already in use", null)
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
