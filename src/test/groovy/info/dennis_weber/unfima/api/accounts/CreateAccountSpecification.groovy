package info.dennis_weber.unfima.api.accounts

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.UnfimaServerBackedApplicationUnderTest

class CreateAccountSpecification extends AbstractUnfimaSpecification {
  def "Creating a new account"() {
    given:
    Map request = ["currencyId"   : UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.id,
                   "accountName"  : "TestBank - Test account",
                   "belongsToUser": true,
                   "notes"        : "",
    ]

    when:
    authenticatedClient.requestSpec({
      it.body({
        it.type("application/json")
        it.text(JsonOutput.toJson(request))
      })
    })
    authenticatedClient.post("/v1.0/accounts")
    def answer = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    authenticatedClient.response.statusCode == 201
    answer.class == Integer
  }

  def "Creating a new account with a bad currency ID"(){
    given:
    int invalidCurrencyId = 999
    Map request = ["currencyId"   : invalidCurrencyId,
                   "accountName"  : "doesNotMatter",
                   "belongsToUser": true,
                   "notes"        : "doesNotMatter",
    ]

    when:
    authenticatedClient.requestSpec({
      it.body({
        it.type("application/json")
        it.text(JsonOutput.toJson(request))
      })
    })
    authenticatedClient.post("/v1.0/accounts")
    def answer = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    authenticatedClient.response.statusCode == 400
    (answer.errorMsg as String).matches("CurrencyID '$invalidCurrencyId' does not exist or belong to user with ID '.*'.")
  }

  def "Creating a new account without a required field"() {
    given:
    Map request = ["currencyId"   : UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.id,
                   // Not that 'accountName' is missing
                   "belongsToUser": true,
                   "notes"        : "",
    ]

    when:
    authenticatedClient.requestSpec({
      it.body({
        it.type("application/json")
        it.text(JsonOutput.toJson(request))
      })
    })
    authenticatedClient.post("/v1.0/accounts")
    def answer = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg == "Required parameter 'accountName' is missing."
    answer.errorId == "REQUIRE_PARAMETER_MISSING"
  }

  def "Creating a new account without a request Body"() {
    when:
    authenticatedClient.post("/v1.0/accounts")
    def answer = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg == "Request body is required but missing"
  }

}
