package info.dennis_weber.unfima.api.currencies

import groovy.json.JsonSlurper
import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.UnfimaServerBackedApplicationUnderTest

class GetSingleCurrencySpecification extends AbstractUnfimaSpecification {
  def "Getting details for a currency"() {
    given:
    // We need to do SOME call first before the call to the tested endpoint, as this first call will initialize the
    // application and the 'testData.id' variable is only known after initialization.
    authenticatedClient.get() // the initialization call

    Map testData = UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency // NOW it is available!

    when:
    authenticatedClient.get("/v1.0/currencies/" + testData.id)
    def answer = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    answer.id == testData.id
    answer.shortName == testData.shortName
    answer.fullName == testData.fullName
    answer.fractionalName == testData.fractionalName
    answer.decimalPlaces == testData.decimalPlaces
    answer.currentRelativeValue == testData.currentExchangeRate // during tests, 3 exchange rates will be set up. Is the current one used here?
  }

  def "Getting details for a non-existing currency"() {
    given:
    int nonExistingCurrencyId = 987

    when:
    authenticatedClient.get("/v1.0/currencies/" + nonExistingCurrencyId)
    def answer = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    authenticatedClient.response.statusCode == 404
    answer.errorMsg.contains("Currency with ID '$nonExistingCurrencyId' does not exist, or does not belong to user")
    answer.errorId == "CURRENCY_NOT_FOUND"
  }

  def "Getting details for a currency that belongs to another user"() {
    given:
    // We need to do SOME call first before the call to the tested endpoint, as this first call will initialize the
    // application and the 'testData.id' variable is only known after initialization.
    authenticatedClient.get() // the initialization call
    int otherUsersCurrencyId = UnfimaServerBackedApplicationUnderTest.TEST_DATA.otherUserCurrency.id

    when:
    authenticatedClient.get("/v1.0/currencies/" + otherUsersCurrencyId)
    def answer = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    authenticatedClient.response.statusCode == 404
    answer.errorMsg.contains("Currency with ID '$otherUsersCurrencyId' does not exist, or does not belong to user")
    answer.errorId == "CURRENCY_NOT_FOUND"
  }
}
