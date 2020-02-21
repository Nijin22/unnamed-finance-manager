package info.dennis_weber.unfima.api.handlers.v1_0.transactions


import com.google.inject.Inject
import groovy.sql.GroovyRowResult
import info.dennis_weber.unfima.api.handlers.v1_0.AbstractAuthenticatedUnfimaHandler
import info.dennis_weber.unfima.api.services.DatabaseService
import ratpack.groovy.handling.GroovyContext

import java.sql.Clob

import static ratpack.jackson.Jackson.json

class ListAllTransactionsHandler extends AbstractAuthenticatedUnfimaHandler {
  @Inject
  DatabaseService dbService

  @Override
  void handleAuthenticated(GroovyContext ctx, int userId) {
    // Query DB
    final String selectStmt = """\
          SELECT tx.transactionId, tx.transactionName, tx.`timestamp`, tx.notes, acc.accountId, acc.accountName, acc.belongsToUser, bc.value, curr.currencyId, curr.shortName as currencyName, curr.decimalPlaces
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
        def notes = row.get("notes")
        if (notes instanceof String) {
          // DB Backend == MariaDB or MySql
          tx.notes = notes
        } else if (notes instanceof Clob) {
          // DB Backend == H2
          tx.notes = (row.get("notes") as Clob).characterStream.text // SQL type is 'TEXT' which does return a 'CLOB'.
        } else {
          throw new RuntimeException("Transaction notes in unexpected format: ${notes.class}")
        }
      }

      // Input and output accounts:
      ResponseAccounts acc = new ResponseAccounts()
      acc.accountId = row.get("accountId") as int
      acc.accountName = row.get("accountName").toString()
      acc.belongsToUser = row.get("belongsToUser")
      acc.value = row.get("value") as BigDecimal
      acc.currencyName = row.get("currencyName")
      if ((row.get("value") as BigDecimal) > 0) {
        tx.outputAccounts.add(acc)
      } else {
        tx.inputAccounts.add(acc)
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

      results.put(txId, tx)
    }

    // Render result
    List asList = results.collect({ return it.value })
    ctx.render(json(asList))
  }
}

final class ResponseForSingleTx {
  int transactionId
  String transactionName
  long timestamp
  String notes
  List<ResponseAccounts> inputAccounts = []
  List<ResponseAccounts> outputAccounts = []
  Map<String, BigDecimal> values = [:]
}

final class ResponseAccounts {
  int accountId
  String accountName
  boolean belongsToUser
  BigDecimal value
  String currencyName
}
