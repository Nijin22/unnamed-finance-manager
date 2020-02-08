package info.dennis_weber.unfima.api.handlers.v1_0.currencies

import com.google.inject.Inject
import groovy.json.JsonException
import groovy.json.JsonSlurper
import info.dennis_weber.unfima.api.errors.BadFormatException
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractAuthenticatedUnfimaHandler
import info.dennis_weber.unfima.api.services.CurrencyDto
import info.dennis_weber.unfima.api.services.CurrencyService
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import ratpack.groovy.handling.GroovyContext

import static ratpack.jackson.Jackson.json

class CreateCurrencyHandler extends AbstractAuthenticatedUnfimaHandler {

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
        CurrencyDto dto = new JsonSlurper().parseText(body.text) as CurrencyDto

        // creating currency
        int currencyId = currencyService.createCurrency(dto, userId)
        ctx.response.status(201)
        ctx.render(json(currencyId))
      } catch (GroovyCastException | JsonException ignored) {
        throw new BadFormatException("Request body is not using the correct schema. Your request body was >>>$body.text<<<")
      }
    })
  }
}