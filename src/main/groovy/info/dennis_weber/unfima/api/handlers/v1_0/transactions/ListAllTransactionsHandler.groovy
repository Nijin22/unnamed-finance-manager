package info.dennis_weber.unfima.api.handlers.v1_0.transactions

import com.google.inject.Inject
import groovy.sql.GroovyRowResult
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractAuthenticatedUnfimaHandler
import info.dennis_weber.unfima.api.services.DatabaseService
import ratpack.groovy.handling.GroovyContext

import static ratpack.jackson.Jackson.json

class ListAllTransactionsHandler extends AbstractAuthenticatedUnfimaHandler {
  @Inject
  DatabaseService dbService

  @Override
  void handleAuthenticated(GroovyContext ctx, int userId) {
    // Query DB
    final String selectStmt = """\
          SELECT tx.transactionId, tx.transactionName, tx.`timestamp`, tx.notes, acc.accountName, bc.value, curr.shortName as currencyName, curr.decimalPlaces
          FROM `balanceChanges` AS bc
          INNER JOIN `transactions` AS tx ON bc.transactionId = tx.transactionId
          INNER JOIN `accounts` AS acc ON bc.accountId = acc.accountId
          INNER JOIN `currencies` AS curr ON acc.currencyId = curr.currencyId
          WHERE tx.userId = ?
          ORDER BY tx.transactionId ASC
          """.stripIndent()
    List<GroovyRowResult> rows = dbService.groovySql.rows(selectStmt, [userId])

    // Build result object
    Map<Integer, ResponseForSingleTx> results = [:]// txId too result

    rows.each { row ->
      int txId = row.get("transactionId") as int
      ResponseForSingleTx tx = results.get(txId)

      // General data:
      if (tx == null) {
        // Transaction needs to be created first
        tx = new ResponseForSingleTx()
        tx.transactionId = txId as int
        tx.transactionName = row.get("transactionName")
        tx.timestamp = row.get("timestamp") as long
        tx.notes = row.get("notes")
      }

      // Input and output accounts:
      String accountName = row.get("accountName").toString()
      if ((row.get("value") as BigDecimal) > 0) {
        tx.outputAccounts.add(accountName)
      } else {
        tx.inputAccounts.add(accountName)
        // I. e. accounts with a value of 0 are counted as "input accounts", but there really is no reason to create a
        // balanceChange for a account when the value is 0.
      }

      // Tx value
      BigDecimal balanceChangeValue = row.get("value") as BigDecimal
      if (balanceChangeValue > 0) {
        // For the 'value' of transactions, we only count positive numbers, as counting positive and negative would equal to 0.
        String currencyName = row.get("currencyName")
        BigDecimal currentValue = tx.values.get(currencyName) ?: new BigDecimal(0)
        BigDecimal newValue = currentValue + balanceChangeValue
        tx.values.put(currencyName, newValue)
      }

      // TODO: Don't simply print account names, they need to include name, isUsersAccount (for highlighting), ID (for linking) and value (possibly for a hover effect)
      // See openApi

      results.put(txId, tx)
    }

    // Render result
    ctx.render(json(results))
  }
}

final class ResponseForSingleTx {
  int transactionId
  String transactionName
  long timestamp
  String notes
  List<String> inputAccounts = []
  List<String> outputAccounts = []
  Map<String, BigDecimal> values = [:]
}
// TODO: Write tests
