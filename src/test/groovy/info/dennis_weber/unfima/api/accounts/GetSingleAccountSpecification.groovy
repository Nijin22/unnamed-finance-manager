package info.dennis_weber.unfima.api.accounts


import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.TestDataProvider

class GetSingleAccountSpecification extends AbstractUnfimaSpecification {
  def "Getting details for a account"() {
    given:
    Map testData = TestDataProvider.TEST_DATA.accounts.ownAccount

    when:
    authenticatedClient.get("/v1.0/accounts/" + testData.id)
    def answer = getResponseObject(authenticatedClient)

    then:
    answer.accountId == testData.id
    answer.currencyId == testData.currencyId
    answer.accountName == testData.accountName
    answer.belongsToUser == testData.belongsToUser
    answer.notes == testData.notes
    answer.currencyName == TestDataProvider.TEST_DATA.currency.shortName
    answer.currentBalance.class == BigDecimal
    BigDecimal expectedBalance = (TestDataProvider.TEST_DATA.transactions.simplePurchase.value + TestDataProvider.TEST_DATA.transactions.threeAccountPurchase.firstAccountBalanceChange).negate()
    answer.currentBalance == expectedBalance
  }

  def "Getting details for a non-existing account"() {
    given:
    int nonExistingAccountId = 987

    when:
    authenticatedClient.get("/v1.0/accounts/" + nonExistingAccountId)
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 404
    answer.errorMsg.contains("Account with ID '$nonExistingAccountId' does not exist, or does not belong to user")
    answer.errorId == "ACCOUNT_NOT_FOUND"
  }

}
