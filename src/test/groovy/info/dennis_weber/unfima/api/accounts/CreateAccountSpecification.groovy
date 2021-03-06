package info.dennis_weber.unfima.api.accounts

import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.TestDataProvider

class CreateAccountSpecification extends AbstractUnfimaSpecification {
  def "Creating a new account"() {
    given:
    Map request = ["currencyId"   : TestDataProvider.TEST_DATA.currency.id,
                   "accountName"  : "TestBank - Test account",
                   "belongsToUser": true,
                   "notes"        : "",
    ]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/accounts")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 201
    answer.class == Integer
  }

  def "Creating a new account with a bad currency ID"() {
    given:
    int invalidCurrencyId = 999
    Map request = ["currencyId"   : invalidCurrencyId,
                   "accountName"  : "doesNotMatter",
                   "belongsToUser": true,
                   "notes"        : "doesNotMatter",
    ]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/accounts")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 400
    (answer.errorMsg as String).matches("CurrencyID '$invalidCurrencyId' does not exist or belong to user with ID '.*'.")
  }

  def "Creating a new account without a required field"() {
    given:
    Map request = ["currencyId"   : TestDataProvider.TEST_DATA.currency.id,
                   // Not that 'accountName' is missing
                   "belongsToUser": true,
                   "notes"        : "",
    ]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/accounts")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg == "Required parameter 'accountName' is missing."
    answer.errorId == "REQUIRE_PARAMETER_MISSING"
  }

  def "Creating a new account without a request Body"() {
    when:
    authenticatedClient.post("/v1.0/accounts")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg == "Request body is required but missing"
  }

  def "Creating a new account but using a way to long accountName"() {
    given:
    String tooLongAccountName = "This account name is way, way, way, way, way, way, way, way, way, way, way, way, way," +
        "way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, " +
        "way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, " +
        "too long."
    Map request = ["currencyId"   : TestDataProvider.TEST_DATA.currency.id,
                   "accountName"  : tooLongAccountName,
                   "belongsToUser": true,
                   "notes"        : "",
    ]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/accounts")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg.contains("Supported maximum length for 'accountName' is 255, but the provided value is ${tooLongAccountName.length()} characters long.")
    answer.errorId == "ATTRIBUTE_TOO_LONG"
  }
}
