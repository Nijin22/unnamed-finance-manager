package info.dennis_weber.unfima.api.handlers.v1_0.currencies

import com.google.inject.Inject
import groovy.json.JsonException
import groovy.json.JsonSlurper
import info.dennis_weber.unfima.api.errors.BadRequestException
import info.dennis_weber.unfima.api.errors.NotFoundException
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractAuthenticatedUnfimaHandler
import info.dennis_weber.unfima.api.services.CurrencyDto
import info.dennis_weber.unfima.api.services.CurrencyService
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import ratpack.groovy.handling.GroovyContext

class UpdateCurrencyHandler extends AbstractAuthenticatedUnfimaHandler {
  @Inject
  CurrencyService currencyService

  @Override
  void handleAuthenticated(GroovyContext ctx, int userId) {
    // does this currency exists and belong to the user?
    int currencyId = ctx.allPathTokens.get("currencyId") as int
    CurrencyDto existingCurrency = currencyService.getCurrency(currencyId, userId)
    if (existingCurrency == null) {
      throw new NotFoundException("Currency with ID '$currencyId' does not exist or belong to user.")
    }

    ctx.request.body.then({ body ->
      // Body missing?
      if (body.contentType.type == null) {
        throw new BadRequestException("Request body is required but missing")
      }

      // Parsing body
      try {
        CurrencyDto dto = new JsonSlurper().parseText(body.text) as CurrencyDto

        if (dto.id != null && dto.id != existingCurrency.id) {
          throw new BadRequestException("You requested to change the currency's ID from '$existingCurrency.id' to '$dto.id', but changing IDs is not possible.")
        }

        // Update currency
        currencyService.updateCurrency(currencyId, dto)
        ctx.response.status(204)
        ctx.response.send() // Send empty
      } catch (GroovyCastException | JsonException ignored) {
        throw new BadRequestException("Request body is not using the correct schema. Your request body was >>>$body.text<<<")
      }
    })
  }
}
