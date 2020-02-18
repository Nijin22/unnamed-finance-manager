package info.dennis_weber.unfima.api.helpers

import groovy.sql.Sql
import info.dennis_weber.unfima.api.services.TimestampHelper
import org.mindrot.jbcrypt.BCrypt

/**
 * This class is used to generate general example data that is available to all running tests 
 */
class TestDataProvider {
  private Sql sql

  static Map<String, Object> TEST_DATA = [
      "user"             : [
          "email"   : "john.doe@example.org",
          "password": "test-password",
          "token"   : "tokenUsedInTests",
          "id"      : 999 // will be replaced at runtime
      ],
      "otherUser"        : [
          "email"   : "another.user@example.org",
          "password": "another-password",
          "id"      : 999 // will be replaced at runtime
      ],
      "currency"         : [
          "id"                 : 999, // will be replaced at runtime
          "shortName"          : "€",
          "fullName"           : "Euro",
          "fractionalName"     : "cent",
          "decimalPlaces"      : 2,
          "currentExchangeRate": new BigDecimal("3.1415")
      ],
      "otherUserCurrency": [
          "id"                 : 999, // will be replaced at runtime
          "shortName"          : "\$",
          "fullName"           : "Dollar",
          "fractionalName"     : "cent",
          "decimalPlaces"      : 2,
          "currentExchangeRate": new BigDecimal("1")
      ],
      "accounts"         : [
          "ownAccount"       : [
              "id"           : 999, // will be replaced at runtime
              "currencyId"   : 999, // will be replaced at runtime
              "accountName"  : "Test Bank - savings account",
              "belongsToUser": true,
              "notes"        : ""
          ],
          "anotherOwnAccount": [
              "id"           : 999, // will be replaced at runtime
              "currencyId"   : 999, // will be replaced at runtime
              "accountName"  : "Cash - Wallet",
              "belongsToUser": true,
              "notes"        : "Money I keep in my wallet"
          ],
          "thirdPartyAccount": [
              "id"           : 999, // will be replaced at runtime
              "currencyId"   : 999, // will be replaced at runtime
              "accountName"  : "Example Store",
              "belongsToUser": false,
              "notes"        : ""
          ]
      ],
      "transactions"     : [
          "simplePurchase"      : [
              "id"              : 999, // will be replaced at runtime
              "name"            : "Buying groceries",
              "timestamp"       : 1582038569,
              "notes"           : "some note about this TX.",
              "inputAccountRef" : "ownAccount", // i.e. get the data from this account
              "outputAccountRef": "thirdPartyAccount", // i.e. get the data from this account
              "value"           : new BigDecimal("10.12")
          ],
          "threeAccountPurchase": [
              "id"                        : 999, // will be replaced at runtime
              "name"                      : "Buying lunch",
              "timestamp"                 : 1582038776,
              "notes"                     : "",
              "inputAccountRef"           : "ownAccount", // i.e. get the data from this account
              "otherInputAccountRef"      : "anotherOwnAccount", // i.e. get the data from this account
              "outputAccountRef"          : "thirdPartyAccount", // i.e. get the data from this account
              "firstAccountBalanceChange" : new BigDecimal("5.12"),
              "secondAccountBalanceChange": new BigDecimal("6.00")
          ]
      ]
  ]

  TestDataProvider(Sql sql) {
    this.sql = sql
  }

  void fillWithTestData() {
    createTestUsers()
    createTestTokens()
    createTestCurrenciesAndExchangeRates()
    createTestAccounts()
    createTestTransactions()
  }

  private void createTestUsers() {
    def autoGeneratedKeys

    // Test users
    String bcryptedPassword = BCrypt.hashpw(TEST_DATA.user.password, BCrypt.gensalt())
    autoGeneratedKeys = sql.executeInsert(
        "INSERT INTO users (email, password) VALUES(?, ?);",
        [TEST_DATA.user.email, bcryptedPassword])
    int userId = autoGeneratedKeys.get(0).get(0) as int
    TEST_DATA.user.put("id", userId)

    // Another test user
    bcryptedPassword = BCrypt.hashpw(TEST_DATA.otherUser.password, BCrypt.gensalt())
    autoGeneratedKeys = sql.executeInsert(
        "INSERT INTO users (email, password) VALUES(?, ?);",
        [TEST_DATA.otherUser.email, bcryptedPassword])
    userId = autoGeneratedKeys.get(0).get(0) as int
    TEST_DATA.otherUser.put("id", userId)
  }

  private createTestTokens() {
    sql.executeInsert(
        "INSERT INTO sessions (token, userId, client, creationTimestamp, lastUsageTimestamp) VALUES(?, ?, ?, ?, ?);",
        [TEST_DATA.user.token, TEST_DATA.user.id, 'Test runner', 0, 0]
    )
  }

  private createTestCurrenciesAndExchangeRates() {
    def autoGeneratedKeys

    // Test currency and exchange rates
    autoGeneratedKeys = sql.executeInsert(
        "INSERT INTO currencies (userId, shortName, fullName, fractionalName, decimalPlaces) VALUES(?, ?, ?, ?, ?);",
        [TEST_DATA.user.id, TEST_DATA.currency.shortName, TEST_DATA.currency.fullName, TEST_DATA.currency.fractionalName, TEST_DATA.currency.decimalPlaces]
    )
    int currencyId = autoGeneratedKeys.get(0).get(0) as int
    TEST_DATA.currency.id = currencyId
    sql.executeInsert( // START exchange rate
        "INSERT INTO currencyExchangeRate (currencyId, startTimestamp, exchangeRate, comment) VALUES(?, ?, ?, ?);",
        [currencyId, 0, new BigDecimal("1"), "start exchange rate, set by test"]
    )
    sql.executeInsert( // CURRENT exchange rate
        "INSERT INTO currencyExchangeRate (currencyId, startTimestamp, exchangeRate, comment) VALUES(?, ?, ?, ?);",
        [currencyId, TimestampHelper.currentTimestamp, TEST_DATA.currency.currentExchangeRate, "JUST has been set, set by test"]
    )
    sql.executeInsert( // FUTURE exchange rate (not sure HOW that should work, but the system allows it!)
        "INSERT INTO currencyExchangeRate (currencyId, startTimestamp, exchangeRate, comment) VALUES(?, ?, ?, ?);",
        [currencyId, 9999999999, new BigDecimal("1"), "Will be available in the future, set by test"]
    )

    // Test currency and exchange rates for other user
    autoGeneratedKeys = sql.executeInsert(
        "INSERT INTO currencies (userId, shortName, fullName, fractionalName, decimalPlaces) VALUES(?, ?, ?, ?, ?);",
        [TEST_DATA.otherUser.id, TEST_DATA.otherUserCurrency.shortName, TEST_DATA.otherUserCurrency.fullName, TEST_DATA.otherUserCurrency.fractionalName, TEST_DATA.otherUserCurrency.decimalPlaces]
    )
    int otherCurrencyId = autoGeneratedKeys.get(0).get(0) as int
    TEST_DATA.otherUserCurrency.id = otherCurrencyId
    sql.executeInsert( // START exchange rate
        "INSERT INTO currencyExchangeRate (currencyId, startTimestamp, exchangeRate, comment) VALUES(?, ?, ?, ?);",
        [otherCurrencyId, 0, TEST_DATA.otherUserCurrency.currentExchangeRate, "start exchange rate, set by test"]
    )
  }

  private void createTestTransactions() {
    def autoGeneratedKeys

    // Test transaction #1
    autoGeneratedKeys = sql.executeInsert(
        "INSERT INTO transactions (userId, transactionName, `timestamp`, notes) VALUES(?, ?, ?, ?)",
        [TEST_DATA.user.id, TEST_DATA.transactions.simplePurchase.name, TEST_DATA.transactions.simplePurchase.timestamp, TEST_DATA.transactions.simplePurchase.notes]
    )
    TEST_DATA.transactions.simplePurchase.id = autoGeneratedKeys[0][0] as int
    sql.executeInsert(
        "INSERT INTO balanceChanges (transactionId, accountId, value) VALUES(?, ?, ?)",
        [TEST_DATA.transactions.simplePurchase.id, TEST_DATA.accounts.get(TEST_DATA.transactions.simplePurchase.inputAccountRef).id, TEST_DATA.transactions.simplePurchase.value.negate()]
    )
    sql.executeInsert(
        "INSERT INTO balanceChanges (transactionId, accountId, value) VALUES(?, ?, ?)",
        [TEST_DATA.transactions.simplePurchase.id, TEST_DATA.accounts.get(TEST_DATA.transactions.simplePurchase.outputAccountRef).id, TEST_DATA.transactions.simplePurchase.value]
    )

    // Test transaction #2
    autoGeneratedKeys = sql.executeInsert(
        "INSERT INTO transactions (userId, transactionName, `timestamp`, notes) VALUES(?, ?, ?, ?)",
        [TEST_DATA.user.id, TEST_DATA.transactions.threeAccountPurchase.name, TEST_DATA.transactions.threeAccountPurchase.timestamp, TEST_DATA.transactions.threeAccountPurchase.notes]
    )
    TEST_DATA.transactions.threeAccountPurchase.id = autoGeneratedKeys[0][0] as int
    sql.executeInsert(
        "INSERT INTO balanceChanges (transactionId, accountId, value) VALUES(?, ?, ?)",
        [TEST_DATA.transactions.threeAccountPurchase.id, TEST_DATA.accounts.get(TEST_DATA.transactions.threeAccountPurchase.inputAccountRef).id, TEST_DATA.transactions.threeAccountPurchase.firstAccountBalanceChange.negate()]
    )
    sql.executeInsert(
        "INSERT INTO balanceChanges (transactionId, accountId, value) VALUES(?, ?, ?)",
        [TEST_DATA.transactions.threeAccountPurchase.id, TEST_DATA.accounts.get(TEST_DATA.transactions.threeAccountPurchase.otherInputAccountRef).id, TEST_DATA.transactions.threeAccountPurchase.secondAccountBalanceChange.negate()]
    )
    BigDecimal sum = TEST_DATA.transactions.threeAccountPurchase.firstAccountBalanceChange + TEST_DATA.transactions.threeAccountPurchase.secondAccountBalanceChange
    sql.executeInsert(
        "INSERT INTO balanceChanges (transactionId, accountId, value) VALUES(?, ?, ?)",
        [TEST_DATA.transactions.threeAccountPurchase.id, TEST_DATA.accounts.get(TEST_DATA.transactions.threeAccountPurchase.outputAccountRef).id, sum]
    )
  }

  private void createTestAccounts() {
    def autoGeneratedKeys

    // Test account
    println TEST_DATA.currency
    autoGeneratedKeys = sql.executeInsert(
        "INSERT INTO accounts (currencyId, accountName, belongsToUser, notes) VALUES(?, ?, ?, ?)",
        [TEST_DATA.currency.id, TEST_DATA.accounts.ownAccount.accountName, TEST_DATA.accounts.ownAccount.belongsToUser, TEST_DATA.accounts.ownAccount.notes]
    )
    TEST_DATA.accounts.ownAccount.id = autoGeneratedKeys[0][0] as int
    TEST_DATA.accounts.ownAccount.currencyId = TEST_DATA.currency.id
    autoGeneratedKeys = sql.executeInsert(
        "INSERT INTO accounts (currencyId, accountName, belongsToUser, notes) VALUES(?, ?, ?, ?)",
        [TEST_DATA.currency.id, TEST_DATA.accounts.anotherOwnAccount.accountName, TEST_DATA.accounts.anotherOwnAccount.belongsToUser, TEST_DATA.accounts.anotherOwnAccount.notes]
    )
    TEST_DATA.accounts.anotherOwnAccount.id = autoGeneratedKeys[0][0] as int
    TEST_DATA.accounts.anotherOwnAccount.currencyId = TEST_DATA.currency.id
    autoGeneratedKeys = sql.executeInsert(
        "INSERT INTO accounts (currencyId, accountName, belongsToUser, notes) VALUES(?, ?, ?, ?)",
        [TEST_DATA.currency.id, TEST_DATA.accounts.thirdPartyAccount.accountName, TEST_DATA.accounts.thirdPartyAccount.belongsToUser, TEST_DATA.accounts.thirdPartyAccount.notes]
    )
    TEST_DATA.accounts.thirdPartyAccount.id = autoGeneratedKeys[0][0] as int
    TEST_DATA.accounts.thirdPartyAccount.currencyId = TEST_DATA.currency.id
  }
}