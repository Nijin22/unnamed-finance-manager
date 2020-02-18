package info.dennis_weber.unfima.api.handlers.v1_0.transactions

import com.google.inject.Inject
import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.sql.Sql
import info.dennis_weber.unfima.api.errors.BadRequestException
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractAuthenticatedUnfimaHandler
import info.dennis_weber.unfima.api.helpers.AbstractDto
import info.dennis_weber.unfima.api.services.AccountDto
import info.dennis_weber.unfima.api.services.AccountService
import info.dennis_weber.unfima.api.services.DatabaseService
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import ratpack.groovy.handling.GroovyContext

import static ratpack.jackson.Jackson.json

class CreateTransactionHandler extends AbstractAuthenticatedUnfimaHandler {
  @Inject
  DatabaseService dbService

  @Inject
  AccountService accountService

  @Override
  void handleAuthenticated(GroovyContext ctx, int userId) {
    ctx.request.body.then({ body ->
      checkBodyIsPresent(body)

      // Parsing body
      try {
        RequestDto dto = new JsonSlurper().parseText(body.text) as RequestDto

        // Check required attributes are present
        checkRequiredAttributesArePresent(dto, ["transactionName", "timestamp", "notes", "balanceChanges"])
        dto.balanceChanges.each {
          checkRequiredAttributesArePresent(it as BalanceChangeDto, ["accountId", "value"])
        }

        checkBalanceChangesEqualToZero(dto.balanceChanges, userId)

        Sql sql = dbService.groovySql
        sql.withTransaction {
          // Insert transaction
          String insertStmt = "INSERT INTO transactions (`userId`, `transactionName`, `timestamp`, `notes`) VALUES(?, ?, ?, ?)"
          List values = [userId, dto.transactionName, dto.timestamp, dto.notes]
          def keys = sql.executeInsert(insertStmt, values)
          int transactionId = keys[0][0] as int

          // Insert balance changes
          dto.balanceChanges.each {
            String insertBcStmt = "INSERT INTO balanceChanges (transactionId, accountId, value) VALUES(?, ?, ?)"
            List bcValues = [transactionId, it.accountId, it.value]
            sql.executeInsert(insertBcStmt, bcValues)
          }

          // Return result
          ctx.response.status(201)
          ctx.render(json(transactionId))
        }

      } catch (GroovyCastException | JsonException ignored) {
        throw new BadRequestException("Request body is not using the correct schema. Your request body was >>>$body.text<<<")
      }
    })
  }

  void checkBalanceChangesEqualToZero(List<BalanceChangeDto> dtos, int userId) {
    Map<Integer, BigDecimal> currenciesToBalance = [:]

    // Add values of all balance changes to map
    dtos.each { bc ->
      AccountDto account = accountService.getBasicDetails(bc.accountId, userId)
      if (account == null) {
        throw new BadRequestException(
            "Cannot create a balance change for account '$bc.accountId' as this account does not exist",
            "ACCOUNT_NOT_VALID")
      }
      Integer currencyId = account.currencyId

      BigDecimal currentBalance = currenciesToBalance.get(currencyId) ?: new BigDecimal(0)
      BigDecimal newBalance = currentBalance.add(bc.value)
      currenciesToBalance.put(currencyId, newBalance)
    }

    // Verify balance changes are 0
    currenciesToBalance.each {
      if (it.value != new BigDecimal(0)) {
        throw new BadRequestException("All involved currencies in a transaction need to equal to 0. " +
            "However the currency with the id '$it.key' has an unbalanced ammount of '$it.value'",
            "UNBALANCED_BALANCE_CHANGES")
      }
    }
  }
}

class RequestDto extends AbstractDto {
  String transactionName
  Long timestamp
  String notes
  List<BalanceChangeDto> balanceChanges

  void setTransactionName(String transactionName) {
    this.transactionName = doAttributeLengthCheck("transactionName", transactionName, 255)
  }

  void setNotes(String notes) {
    this.notes = doAttributeLengthCheck("notes", notes, 16777215)
  }
}

final class BalanceChangeDto extends AbstractDto {
  int accountId
  BigDecimal value
}
