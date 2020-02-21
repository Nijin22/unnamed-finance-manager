package info.dennis_weber.unfima.api.handlers.v1_0.accounts

import com.google.inject.Inject
import info.dennis_weber.unfima.api.errors.NotFoundException
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractAuthenticatedUnfimaHandler
import info.dennis_weber.unfima.api.services.AccountDto
import info.dennis_weber.unfima.api.services.AccountService
import info.dennis_weber.unfima.api.services.CurrencyDto
import info.dennis_weber.unfima.api.services.CurrencyService
import ratpack.groovy.handling.GroovyContext

import static ratpack.jackson.Jackson.json

class ListSingleAccountHandler extends AbstractAuthenticatedUnfimaHandler {
  @Inject
  AccountService accountService

  @Override
  void handleAuthenticated(GroovyContext ctx, int userId) {
    int accountId = ctx.allPathTokens.get("accountId") as int

    AccountDto dto = accountService.getDetails(accountId, userId)
    if (dto == null) {
      throw new NotFoundException("Account with ID '$accountId' does not exist, or does not belong to user '$userId'".toString(), "ACCOUNT_NOT_FOUND")
    } else {
      ctx.render(json(dto.toMapWithoutNull()))
    }
  }
}
