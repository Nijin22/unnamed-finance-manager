package info.dennis_weber.unfima.api.handlers.v1_0.currencies

import com.google.inject.Inject
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractAuthenticatedUnfimaHandler
import info.dennis_weber.unfima.api.services.CurrencyDto
import info.dennis_weber.unfima.api.services.CurrencyService
import ratpack.groovy.handling.GroovyContext

import static ratpack.jackson.Jackson.json

class ListAllCurrenciesHandler extends AbstractAuthenticatedUnfimaHandler {
  @Inject
  CurrencyService currencyService

  @Override
  void handleAuthenticated(GroovyContext ctx, int userId) {
    List<CurrencyDto> dtos = currencyService.getAllCurrencies(userId)
    List<Map> toRender = []
    dtos.each {
      toRender.add(it.toMapWithoutNull())
    }
    ctx.render(json(toRender))
  }
}
