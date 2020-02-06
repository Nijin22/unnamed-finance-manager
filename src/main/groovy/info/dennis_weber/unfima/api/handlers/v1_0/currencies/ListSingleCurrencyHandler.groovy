package info.dennis_weber.unfima.api.handlers.v1_0.currencies

import com.google.inject.Inject
import info.dennis_weber.unfima.api.errors.NotFoundException
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractAuthenticatedUnfimaHandler
import info.dennis_weber.unfima.api.services.CurrencyDto
import info.dennis_weber.unfima.api.services.CurrencyService
import ratpack.groovy.handling.GroovyContext

import static ratpack.jackson.Jackson.json

class ListSingleCurrencyHandler extends AbstractAuthenticatedUnfimaHandler {
  @Inject
  CurrencyService currencyService

  @Override
  void handleAuthenticated(GroovyContext ctx, int userId) {
    int currencyId = ctx.allPathTokens.get("currencyId") as int

    CurrencyDto dto = currencyService.getCurrency(currencyId, userId)
    if (dto == null) {
      throw new NotFoundException("Currency with ID '$currencyId' does not exist, or does not belong to user '$userId'".toString(), "CURRENCY_NOT_FOUND")
    } else {
      ctx.render(json(dto.toMapWithoutNull()))
    }
  }
}
