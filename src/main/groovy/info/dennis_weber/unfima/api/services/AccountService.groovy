package info.dennis_weber.unfima.api.services

import com.google.inject.Inject
import groovy.sql.GroovyRowResult
import info.dennis_weber.unfima.api.helpers.AbstractDto

class AccountService {

  @Inject
  DatabaseService dbService

  /**
   * Get very basic details about a given account id. This uses a pretty quick database call and should be used when
   * more details are not required.
   *
   * @param accountId
   * @param userId
   * @return AccountDto with the following fields filled:
   *         (accountId, currencyId, accountName, belongsToUser, notes). All other fields are null.
   *         Or null if either the accountId wasn't found or it did not belong to the given user.
   */
  AccountDto getBasicDetails(int accountId, int userId) {
    final String selectStmt = """\
        SELECT acc.accountId, curr.currencyId, acc.accountName, acc.belongsToUser, acc.notes
        FROM accounts AS acc
        JOIN currencies AS curr
          ON acc.currencyId  = curr.currencyId
        WHERE curr.userId = ?
          AND acc.accountId = ?
        """.stripIndent()
    GroovyRowResult row = dbService.groovySql.firstRow(selectStmt, [userId, accountId])
    if (row == null) {
      return null
    }

    AccountDto result = new AccountDto()

    result.accountId = row.get("accountId") as Integer
    result.currencyId = row.get("currencyId") as Integer
    result.accountName = row.get("accountName")
    result.belongsToUser = row.get("belongsToUser") as Boolean
    result.notes = row.get("notes")

    return result
  }

  /**
   * Get all details available for a given account ID.
   *
   * @param accountId
   * @param userId
   * @return dto with all fields filled, or null if accountId does not exist or does not belong to the userId
   */
  AccountDto getDetails(int accountId, int userId) {
    final String selectStatement = "SELECT * FROM accountsWithBalance WHERE userId = ? AND accountId = ?"
    GroovyRowResult row = dbService.groovySql.firstRow(selectStatement, [userId, accountId])

    if (row == null) {
      return null
    }

    return extractDtoFromRow(row)
  }

  private static AccountDto extractDtoFromRow(GroovyRowResult row) {
    AccountDto dto = new AccountDto()

    dto.accountId = row.get("accountId") as Integer
    dto.currencyId = row.get("currencyId") as Integer
    dto.accountName = row.get("accountName")
    dto.belongsToUser = row.get("belongsToUser") as Boolean
    dto.notes = DatabaseService.convertPossibleClobToString(row.get("notes"))
    dto.currencyName = row.get("currencyName")
    dto.currentBalance = row.get("currentBalance") as BigDecimal

    return dto
  }
}

final class AccountDto extends AbstractDto {
  Integer accountId
  Integer currencyId
  String accountName
  Boolean belongsToUser
  String notes
  String currencyName
  BigDecimal currentBalance

  //////////////////////
  // Checked Setters: //
  //////////////////////
  void setAccountName(String accountName) {
    this.accountName = doAttributeLengthCheck("accountName", accountName, 255)
  }

  void setNotes(String notes) {
    this.notes = doAttributeLengthCheck("notes", notes, 16777215)
  }

}
