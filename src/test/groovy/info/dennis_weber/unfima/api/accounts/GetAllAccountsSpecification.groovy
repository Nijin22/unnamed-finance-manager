package info.dennis_weber.unfima.api.accounts

import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.TestDataProvider

class GetAllAccountsSpecification extends AbstractUnfimaSpecification {
  def "Getting details for all accounts"() {
    when:
    authenticatedClient.get("/v1.0/accounts")
    List<Map> answer = getResponseObject(authenticatedClient) as List<Map>

    then:
    answer.size() == 3 // In the tests, there are 3 accounts
    answer.any { it.accountName == TestDataProvider.TEST_DATA.accounts.ownAccount.accountName }
    answer.any { it.accountName == TestDataProvider.TEST_DATA.accounts.anotherOwnAccount.accountName }
    answer.any { it.accountName == TestDataProvider.TEST_DATA.accounts.thirdPartyAccount.accountName }
  }

  def "Getting details for belonging accounts"() {
    when:
    authenticatedClient.get("/v1.0/accounts?onlyBelongingAccounts=true")
    List<Map> answer = getResponseObject(authenticatedClient) as List<Map>

    then:
    answer.size() == 2 // In the tests, there are 2 own-accounts
    answer.any { it.accountName == TestDataProvider.TEST_DATA.accounts.ownAccount.accountName }
    answer.any { it.accountName == TestDataProvider.TEST_DATA.accounts.anotherOwnAccount.accountName }
  }

  def "Getting details for third-party accounts"() {
    when:
    authenticatedClient.get("/v1.0/accounts?onlyThirdPartyAccounts=true")
    List<Map> answer = getResponseObject(authenticatedClient) as List<Map>

    then:
    answer.size() == 1 // In the tests, there is only one third-party account
    answer.first().accountName == TestDataProvider.TEST_DATA.accounts.thirdPartyAccount.accountName
  }

  def "Getting details for accounts, but filtering belonging accounts AND third-party accounts"() {
    when:
    authenticatedClient.get("/v1.0/accounts?onlyBelongingAccounts=true&onlyThirdPartyAccounts=true")
    List<Map> answer = getResponseObject(authenticatedClient) as List<Map>

    then:
    answer.size() == 0 // Setting the filter to mutually exclusive values should return an empty set.
  }
}
