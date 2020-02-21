package info.dennis_weber.unfima.api.handlers.v1_0.accounts

import com.google.inject.Inject
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractAuthenticatedUnfimaHandler
import info.dennis_weber.unfima.api.services.AccountDto
import info.dennis_weber.unfima.api.services.AccountService
import ratpack.groovy.handling.GroovyContext

import static ratpack.jackson.Jackson.json

class ListAllAccountsHandler extends AbstractAuthenticatedUnfimaHandler {
  @Inject
  AccountService accountService

  @Override
  void handleAuthenticated(GroovyContext ctx, int userId) {
    boolean onlyBelongingAccounts = ctx.request.queryParams.get("onlyBelongingAccounts") ?: false
    boolean onlyThirdPartyAccounts = ctx.request.queryParams.get("onlyThirdPartyAccounts") ?: false

    List<AccountDto> dtos = accountService.getAllAccounts(userId, onlyBelongingAccounts, onlyThirdPartyAccounts)
    List<Map> toRender = []
    dtos.each {
      toRender.add(it.toMapWithoutNull())
    }
    ctx.render(json(toRender))
  }
}
