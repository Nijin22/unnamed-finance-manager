package info.dennis_weber.unfima.api.handlers.v1_0.users

import com.google.inject.Inject
import groovy.sql.GroovyRowResult
import info.dennis_weber.unfima.api.errors.BadAuthenticationException
import info.dennis_weber.unfima.api.errors.BadFormatException
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractUnfimaHandler
import info.dennis_weber.unfima.api.services.DatabaseService
import org.mindrot.jbcrypt.BCrypt
import ratpack.groovy.handling.GroovyContext

import java.security.SecureRandom

import static ratpack.jackson.Jackson.json

class AuthenticateHandler extends AbstractUnfimaHandler {
  private static final SecureRandom secureRandom = new SecureRandom()

  @Inject
  DatabaseService dbService

  @Override
  protected void handle(GroovyContext ctx) {
    ctx.parse(RequestBody.class).then({
      body ->

        // verify email and pw are in request is set and valid
        if (body.email == null) {
          throw new BadFormatException("required parameter 'email' is missing", null)
        }
        if (body.password == null) {
          throw new BadFormatException("required parameter 'password' is missing", null)
        }

        // verify client is present and valid
        if (body.client == null) {
          throw new BadFormatException("required parameter 'client' is missing", null)
        }
        if (body.client.size() > 255) {
          throw new BadFormatException("'client' is ${body.client.size()} characters long, limit is 255.", null)
        }

        // Get stored password
        String selectPasswordStatement = "SELECT id, password FROM users where email = ?"
        GroovyRowResult dbResult = dbService.getGroovySql().firstRow(selectPasswordStatement, [body.email])
        if (dbResult == null) {
          // no stored password found --> wrong username
          throw new BadAuthenticationException("Email address '$body.email' not found", "USERNAME_UNKNOWN")
        }
        int userId = dbResult.get("id") as int
        String storedPw = new String(dbResult.get("password") as char[])

        if (BCrypt.checkpw(body.password, storedPw)) {
          // pw okay

          // store token in database
          String token = generateNewToken()
          String insertTokenStatement = "INSERT INTO `sessions` (token, userId, client, creationTimestamp, lastUsageTimestamp) VALUES(?, ?, ?, ?, ?)"
          int timestamp = (new Date().getTime() / 1000).toInteger()
          dbService.getGroovySql().executeInsert(insertTokenStatement, [token, userId, body.client, timestamp, timestamp])

          // render results
          ctx.response.status(200)
          ctx.render(json(["bearerToken": token]))
        } else {
          // pw doesn't match
          throw new BadAuthenticationException("Password doesn't match email '$body.email'", "PASSWORD_DOES_NOT_MATCH")
        }

    })
  }

  static private class RequestBody {
    String email
    String password
    String client
  }

  static String generateNewToken() {
    // see: https://stackoverflow.com/a/56628391/3298787

    final Base64.Encoder base64Encoder = Base64.getUrlEncoder()
    byte[] randomBytes = new byte[24]
    secureRandom.nextBytes(randomBytes)
    return base64Encoder.encodeToString(randomBytes)
  }
}
