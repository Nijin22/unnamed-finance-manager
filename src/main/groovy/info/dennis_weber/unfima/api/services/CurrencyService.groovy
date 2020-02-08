package info.dennis_weber.unfima.api.services

import com.google.inject.Inject
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import info.dennis_weber.unfima.api.helpers.MappableTrait

class CurrencyService {
  @Inject
  DatabaseService dbService

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
    String selectCurrencyQuery = """SELECT * FROM currenciesWithCurrentExchangeRate
                                    WHERE userId = ?
                                      AND currencyId = ?""".stripIndent()
    GroovyRowResult row = dbService.getGroovySql().firstRow(selectCurrencyQuery, [userId, currencyId])
    if (row == null) {
      return null
    }

    CurrencyDto dto = extractDtoFromSqlRow(row)
    return dto
  }

  List<CurrencyDto> getAllCurrencies(int userId) {
    String selectCurrenciesQuery = """SELECT * FROM currenciesWithCurrentExchangeRate
                                    WHERE userId = ?""".stripIndent()
    List<GroovyRowResult> rows = dbService.getGroovySql().rows(selectCurrenciesQuery, [userId])

    List<CurrencyDto> result = []
    rows.each {
      result.add(extractDtoFromSqlRow(it))
    }
    return result
  }

  private static CurrencyDto extractDtoFromSqlRow(GroovyRowResult row) {
    CurrencyDto dto = new CurrencyDto()
    dto.id = row.get("currencyId") as int
    dto.shortName = row.get("shortName")
    dto.fullName = row.get("fullName")
    dto.fractionalName = row.get("fractionalName")
    dto.decimalPlaces = row.get("decimalPlaces") as int
    dto.currentRelativeValue = row.get("exchangeRate") as BigDecimal
    dto
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
