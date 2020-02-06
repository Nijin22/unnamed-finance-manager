package info.dennis_weber.unfima.api.services

import com.google.inject.Inject
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import info.dennis_weber.unfima.api.helpers.MappableTrait

class CurrencyService {
  @Inject
  DatabaseService dbService

  final String selectCurrenciesWithCurrentExchangeRateStatement = """\
    SELECT currencies.*, rates.exchangeRate 
    FROM currencyExchangeRate as rates
    INNER JOIN
        (SELECT currencyId, MAX(startTimestamp) AS maxCurrentTimestamp
        FROM currencyExchangeRate
        WHERE startTimestamp <= ? -- >>>>>>> Timestamp
        GROUP BY currencyId) as maxCurrentTimestamps
    ON rates.currencyId = maxCurrentTimestamps.currencyId AND rates.startTimestamp = maxCurrentTimestamps.maxCurrentTimestamp
    INNER JOIN currencies 
      ON rates.currencyId = currencies.currencyId
    WHERE userId = ? -- >>>>>>> UserId
    """.stripIndent()

  int createCurrency(CurrencyDto dto, int userId) {
    Sql sql = dbService.getGroovySql()

    // Create currency
    def keys = sql.executeInsert(
        "INSERT INTO currencies (userId, shortName, fullName, fractionalName, decimalPlaces) VALUES(?, ?, ?, ?, ?)",
        [userId, dto.shortName, dto.fullName, dto.fractionalName, dto.decimalPlaces]
    )
    int currencyId = keys[0][0] as int

    // Create first currency exchange rate
    sql.executeInsert(
        "INSERT INTO currencyExchangeRate (currencyId, startTimestamp, exchangeRate, comment) VALUES(?, ?, ?, ?)",
        [currencyId, 0, dto.starterRelativeValue, "Initial exchange rate"]
    )
    return currencyId

  }

  /**
   * Retrieves a currency from the database
   * A currency is only returned if the ID is correct AND the currency belongs to the provided user.
   *
   * @param currencyId identifier to find currency
   * @param userId user trying to find that currency
   * @return the found currency, or null if not found or not the correct user
   */
  CurrencyDto getCurrency(int currencyId, int userId) {
    String selectCurrencyQuery = selectCurrenciesWithCurrentExchangeRateStatement +
        "AND currencies.currencyId = ?"
    GroovyRowResult row = dbService.getGroovySql().firstRow(selectCurrencyQuery, [TimestampHelper.getCurrentTimestamp(), userId, currencyId])
    if (row == null) {
      return null
    }

    CurrencyDto dto = new CurrencyDto()
    dto.id = row.get("currencyId") as int
    dto.shortName = row.get("shortName")
    dto.fullName = row.get("fullName")
    dto.fractionalName = row.get("fractionalName")
    dto.decimalPlaces = row.get("decimalPlaces") as int
    dto.currentRelativeValue = row.get("exchangeRate") as BigDecimal

    return dto
  }
}

final class CurrencyDto implements MappableTrait {
  int id
  String shortName
  String fullName
  String fractionalName
  int decimalPlaces
  BigDecimal starterRelativeValue
  BigDecimal currentRelativeValue
}
