package info.dennis_weber.unfima.api.handlers.v1_0.users

import com.google.inject.Inject
import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import info.dennis_weber.unfima.api.errors.BadRequestException
import info.dennis_weber.unfima.api.errors.ConflictException
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractUnfimaHandler
import info.dennis_weber.unfima.api.services.CurrencyDto
import info.dennis_weber.unfima.api.services.CurrencyService
import info.dennis_weber.unfima.api.services.DatabaseService
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import org.mindrot.jbcrypt.BCrypt
import ratpack.groovy.handling.GroovyContext

import java.sql.SQLIntegrityConstraintViolationException

class RegisterAccountHandler extends AbstractUnfimaHandler {

  @Inject
  DatabaseService dbService

  @Inject
  CurrencyService currencyService

  @Override
  protected void handle(GroovyContext ctx) {
    final int MAX_EMAIL_LENGTH = 255
    final int MAX_PASSWORD_LENGTH = 100

    ctx.request.body.then({ body ->
      checkBodyIsPresent(body)

      // Parse body
      try {
        RequestBody dto = new JsonSlurper().parseText(body.text) as RequestBody

        // verify email is set and valid
        if (dto.email == null) {
          throw new BadRequestException("required parameter 'email' is missing")
        }
        if (dto.email.length() > MAX_EMAIL_LENGTH) {
          throw new BadRequestException("'email' parameter is too long.")
        }

        // Verify PW is set and valid
        if (dto.password == null) {
          throw new BadRequestException("required parameter 'password' is missing")
        }
        if (dto.password.length() > MAX_PASSWORD_LENGTH) {
          throw new BadRequestException("'password' parameter is too long")
        }

        // Verify all required currency values are set
        if (dto.starterCurrency == null) throw new BadRequestException("required parameter 'starterCurrency' is missing")
        List requiredValues = ["shortName", "fullName", "fractionalName", "decimalPlaces"]
        requiredValues.each {
          if (dto.starterCurrency.getProperty(it) == null) {
            throw new BadRequestException("required parameter '$it' of 'starterCurrency' is missing.")
          }
        }

        // Convert password in a format save for storage
        String hashedPassword = BCrypt.hashpw(dto.password, BCrypt.gensalt())

        // Check if user is already in db
        String selectStatement = "SELECT COUNT(*) as count FROM `users` WHERE `email` = ?"
        GroovyRowResult row = dbService.getGroovySql().firstRow(selectStatement, [dto.email])
        if (row.get("count") > 0) {
          throw new ConflictException("email address is already in use", null)
        }

        // Store new user
        Sql sql = dbService.getGroovySql()
        String insertStatement = "INSERT INTO `users` (email, password) VALUES (?, ?)"
        try {
          def keys = sql.executeInsert(insertStatement, [dto.email, hashedPassword])
          int userId = keys[0][0] as int

          try {
            // Store currency for user
            // the first currency should have a exchange rate of '1':
            dto.starterCurrency.starterRelativeValue = new BigDecimal(1)
            int currencyId = currencyService.createCurrency(dto.starterCurrency, userId)

            // Set default currency
            dbService.getGroovySql().executeUpdate(
                "UPDATE users SET defaultCurrency=? WHERE id=?",
                [currencyId, userId]
            )
          } catch (Exception e) {
            // Something failed when creating the starter currency. --> Delete the user again.
            sql.execute("DELETE FROM users WHERE id=?", [userId])
            throw e
          }


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

      } catch (GroovyCastException | JsonException ignored) {
        throw new BadRequestException("Request body is not using the correct schema. Your request body was >>>$body.text<<<")
      }
    })
  }

  static private class RequestBody {
    String email
    String password
    CurrencyDto starterCurrency
  }
}
