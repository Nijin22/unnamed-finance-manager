package info.dennis_weber.unfima.api.handlers.v1_0.accounts

import com.google.inject.Inject
import groovy.json.JsonException
import groovy.json.JsonSlurper
import info.dennis_weber.unfima.api.errors.BadFormatException
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractAuthenticatedUnfimaHandler
import info.dennis_weber.unfima.api.services.AccountDto
import info.dennis_weber.unfima.api.services.CurrencyService
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import ratpack.groovy.handling.GroovyContext

import static ratpack.jackson.Jackson.json

class CreateAccountHandler extends AbstractAuthenticatedUnfimaHandler {

  @Inject
  CurrencyService currencyService

  @Override
  void handleAuthenticated(GroovyContext ctx, int userId) {
    ctx.request.body.then({ body ->
      // Body missing?
      if (body.contentType.type == null) {
        throw new BadFormatException("Request body is required but missing")
      }

      // Parsing body
      try {
        AccountDto dto = new JsonSlurper().parseText(body.text) as AccountDto
        checkIfAllRequiredParametersAreFilled(dto)
        checkIfCurrencyIdIsValid(dto, userId)

        // creating account
        String insertStmt = "INSERT INTO accounts (currencyId, accountName, belongsToUser, notes) VALUES(?, ?, ?, ?)"
        List values = [dto.currencyId, dto.accountName, dto.belongsToUser, dto.notes]
        def keys = dbService.groovySql.executeInsert(insertStmt, values)
        int accountId = keys[0][0] as int

        // return result
        ctx.response.status(201)
        ctx.render(json(accountId))

      } catch (GroovyCastException | JsonException ignored) {
        throw new BadFormatException("Request body is not using the correct schema. Your request body was >>>$body.text<<<")
      }
    })
  }

  private void checkIfCurrencyIdIsValid(AccountDto dto, int userId) {
    if (currencyService.getCurrency(dto.currencyId, userId) == null) {
      throw new BadFormatException("CurrencyID '$dto.currencyId' does not exist or belong to user with ID '$userId'.")
    }
  }

  private static void checkIfAllRequiredParametersAreFilled(AccountDto dto) {
    final List requiredParameters = ["currencyId", "accountName", "belongsToUser", "notes"]
    requiredParameters.each {
      if (dto.getProperty(it) == null) {
        throw new BadFormatException("Required parameter '$it' is missing.", "REQUIRE_PARAMETER_MISSING")
      }
    }
  }
}