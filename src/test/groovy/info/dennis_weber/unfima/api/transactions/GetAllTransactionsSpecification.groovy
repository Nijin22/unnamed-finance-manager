package info.dennis_weber.unfima.api.transactions

import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.TestDataProvider
import info.dennis_weber.unfima.api.services.TimestampHelper

class GetAllTransactionsSpecification extends AbstractUnfimaSpecification {
  def "Getting all transactions for a user"() {
    given:
    Map expectedFirst = TestDataProvider.TEST_DATA.transactions.simplePurchase
    Map expectedSecond = TestDataProvider.TEST_DATA.transactions.threeAccountPurchase

    when:
    authenticatedClient.get("/v1.0/transactions")
    List<Map> answer = getResponseObject(authenticatedClient) as List<Map>

    then:
    answer instanceof List
    answer.size() == 2
    def actualFirst = answer.first() // first TX
    def actualSecond = answer.get(1) // second TX

    actualFirst.transactionId == expectedFirst.id
    actualFirst.transactionName == expectedFirst.name
    actualFirst.timestamp == expectedFirst.timestamp
    actualFirst.notes == expectedFirst.notes
    actualFirst.inputAccounts.first().accountId == TestDataProvider.TEST_DATA.accounts.get(expectedFirst.inputAccountRef).id
    actualFirst.inputAccounts.first().accountName == TestDataProvider.TEST_DATA.accounts.get(expectedFirst.inputAccountRef).accountName
    actualFirst.inputAccounts.first().belongsToUser == TestDataProvider.TEST_DATA.accounts.get(expectedFirst.inputAccountRef).belongsToUser
    actualFirst.inputAccounts.first().value == expectedFirst.value.negate()
    actualFirst.inputAccounts.first().currencyName == TestDataProvider.TEST_DATA.currency.shortName
    actualFirst.outputAccounts.first().accountId == TestDataProvider.TEST_DATA.accounts.get(expectedFirst.outputAccountRef).id
    actualFirst.outputAccounts.first().accountName == TestDataProvider.TEST_DATA.accounts.get(expectedFirst.outputAccountRef).accountName
    actualFirst.outputAccounts.first().belongsToUser == TestDataProvider.TEST_DATA.accounts.get(expectedFirst.outputAccountRef).belongsToUser
    actualFirst.outputAccounts.first().value == expectedFirst.value
    actualFirst.outputAccounts.first().currencyName == TestDataProvider.TEST_DATA.currency.shortName
    actualFirst.values.get("€") != null
    actualFirst.values.get("€") == expectedFirst.value

    actualSecond.transactionId == expectedSecond.id
    actualSecond.transactionName == expectedSecond.name
    actualSecond.timestamp == expectedSecond.timestamp
    actualSecond.notes == expectedSecond.notes
    actualSecond.inputAccounts.first().accountId == TestDataProvider.TEST_DATA.accounts.get(expectedSecond.inputAccountRef).id
    actualSecond.inputAccounts.first().accountName == TestDataProvider.TEST_DATA.accounts.get(expectedSecond.inputAccountRef).accountName
    actualSecond.inputAccounts.first().belongsToUser == TestDataProvider.TEST_DATA.accounts.get(expectedSecond.inputAccountRef).belongsToUser
    actualSecond.inputAccounts.first().value == expectedSecond.firstAccountBalanceChange.negate()
    actualSecond.inputAccounts.first().currencyName == TestDataProvider.TEST_DATA.currency.shortName
    actualSecond.inputAccounts.get(1).accountId == TestDataProvider.TEST_DATA.accounts.get(expectedSecond.otherInputAccountRef).id
    actualSecond.inputAccounts.get(1).accountName == TestDataProvider.TEST_DATA.accounts.get(expectedSecond.otherInputAccountRef).accountName
    actualSecond.inputAccounts.get(1).belongsToUser == TestDataProvider.TEST_DATA.accounts.get(expectedSecond.otherInputAccountRef).belongsToUser
    actualSecond.inputAccounts.get(1).value == expectedSecond.secondAccountBalanceChange.negate()
    actualSecond.inputAccounts.get(1).currencyName == TestDataProvider.TEST_DATA.currency.shortName
    actualSecond.outputAccounts.first().accountId == TestDataProvider.TEST_DATA.accounts.get(expectedSecond.outputAccountRef).id
    actualSecond.outputAccounts.first().accountName == TestDataProvider.TEST_DATA.accounts.get(expectedSecond.outputAccountRef).accountName
    actualSecond.outputAccounts.first().belongsToUser == TestDataProvider.TEST_DATA.accounts.get(expectedSecond.outputAccountRef).belongsToUser
    actualSecond.outputAccounts.first().value == expectedSecond.firstAccountBalanceChange + expectedSecond.secondAccountBalanceChange
    actualSecond.outputAccounts.first().currencyName == TestDataProvider.TEST_DATA.currency.shortName
    actualSecond.values.get("€") != null
    actualSecond.values.get("€") == expectedSecond.firstAccountBalanceChange + expectedSecond.secondAccountBalanceChange
  }

  def "Adding a new transaction and retrieving the list of all transactions"() {
    given:
    int CURRENT_TRANSACTIONS = 2
    Map addRequestPayload = [
        "transactionName": "a new TX",
        "timestamp"      : TimestampHelper.currentTimestamp,
        "notes"          : "My new TX",
        balanceChanges   : [
            [
                "accountId": TestDataProvider.TEST_DATA.accounts.ownAccount.id,
                "value"    : new BigDecimal("-1.23")
            ],
            [
                "accountId": TestDataProvider.TEST_DATA.accounts.thirdPartyAccount.id,
                "value"    : new BigDecimal("1.23")
            ]
        ]
    ]

    when:
    // create the new TX
    setRequestBody(authenticatedClient, addRequestPayload)
    authenticatedClient.post("/v1.0/transactions")
    int idOfNewTx = getResponseObject(authenticatedClient) as int

    // retrieve the new list of transactions
    authenticatedClient.get("/v1.0/transactions")
    List<Map> answer = getResponseObject(authenticatedClient) as List<Map>

    then:
    answer.size() == CURRENT_TRANSACTIONS + 1
    def newTx = answer.get(CURRENT_TRANSACTIONS)

    newTx.transactionId == idOfNewTx
    newTx.transactionName == addRequestPayload.transactionName
    newTx.timestamp == addRequestPayload.timestamp
    newTx.notes == addRequestPayload.notes
    newTx.inputAccounts.first().accountId == addRequestPayload.balanceChanges.get(0).accountId
    newTx.inputAccounts.first().value == addRequestPayload.balanceChanges.get(0).value
    newTx.outputAccounts.first().accountId == addRequestPayload.balanceChanges.get(1).accountId
    newTx.outputAccounts.first().value == addRequestPayload.balanceChanges.get(1).value
    newTx.values.get("€") != null
    newTx.values.get("€") == addRequestPayload.balanceChanges.get(1).value
  }
}
