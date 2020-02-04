package info.dennis_weber.unfima.api.services

import com.google.inject.Inject
import groovy.sql.Sql

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
}

final class CurrencyDto {
  int id
  String shortName
  String fullName
  String fractionalName
  String decimalPlaces
  BigDecimal starterRelativeValue
  BigDecimal currentRelativeValue
}
