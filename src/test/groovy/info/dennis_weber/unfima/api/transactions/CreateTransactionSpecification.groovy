package info.dennis_weber.unfima.api.transactions

import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.TestDataProvider
import info.dennis_weber.unfima.api.services.TimestampHelper

class CreateTransactionSpecification extends AbstractUnfimaSpecification {
  def "Creating a new transaction"() {
    given:
    Map request = ["transactionName": "Buying example groceries",
                   "timestamp"      : TimestampHelper.currentTimestamp,
                   "notes"          : "",
                   balanceChanges   : [
                       [
                           "accountId": TestDataProvider.TEST_DATA.accounts.ownAccount.id,
                           "value"    : new BigDecimal("-10")
                       ],
                       [
                           "accountId": TestDataProvider.TEST_DATA.accounts.thirdPartyAccount.id,
                           "value"    : new BigDecimal("10")
                       ]
                   ]
    ]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/transactions")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 201
    answer.class == Integer
  }

  def "Creating a new transaction but missing a required attribute"() {
    given:
    Map request = ["transactionName": "Buying example groceries",
                   "timestamp"      : TimestampHelper.currentTimestamp,
                   "notes"          : "",
                   // Note that the balance changes are missing
    ]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/transactions")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg == "Required parameter 'balanceChanges' is missing."
    answer.errorId == "REQUIRE_PARAMETER_MISSING"
  }

  def "Creating a new transaction with unbalanced 'balance changes'"() {
    given:
    Map request = ["transactionName": "Buying example groceries",
                   "timestamp"      : TimestampHelper.currentTimestamp,
                   "notes"          : "",
                   balanceChanges   : [
                       [
                           "accountId": TestDataProvider.TEST_DATA.accounts.ownAccount.id,
                           "value"    : new BigDecimal("-10")
                       ],
                       [
                           "accountId": TestDataProvider.TEST_DATA.accounts.thirdPartyAccount.id,
                           "value"    : new BigDecimal("10.01")
                       ]
                   ]
    ]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/transactions")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg.startsWith("All involved currencies in a transaction need to equal to 0.")
    answer.errorId == "UNBALANCED_BALANCE_CHANGES"
  }

  def "Creating a new transaction with three affected accounts"() {
    given:
    Map request = ["transactionName": "Buying example groceries",
                   "timestamp"      : TimestampHelper.currentTimestamp,
                   "notes"          : "",
                   balanceChanges   : [
                       [
                           "accountId": TestDataProvider.TEST_DATA.accounts.ownAccount.id,
                           "value"    : new BigDecimal("-10")
                       ],
                       [
                           "accountId": TestDataProvider.TEST_DATA.accounts.anotherOwnAccount.id,
                           "value"    : new BigDecimal("-5.12")
                       ],
                       [
                           "accountId": TestDataProvider.TEST_DATA.accounts.thirdPartyAccount.id,
                           "value"    : new BigDecimal("15.12")
                       ]
                   ]
    ]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/transactions")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 201
    answer.class == Integer
  }

  def "Creating a new transaction with an invalid account id"() {
    given:
    int invalidAccountId = 987
    Map request = ["transactionName": "Buying example groceries",
                   "timestamp"      : TimestampHelper.currentTimestamp,
                   "notes"          : "",
                   balanceChanges   : [
                       [
                           "accountId": invalidAccountId,
                           "value"    : new BigDecimal("-10")
                       ],
                       [
                           "accountId": TestDataProvider.TEST_DATA.accounts.thirdPartyAccount.id,
                           "value"    : new BigDecimal("10")
                       ]
                   ]
    ]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/transactions")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg == "Cannot create a balance change for account '$invalidAccountId' as this account does not exist"
    answer.errorId == "ACCOUNT_NOT_VALID"
  }

}
