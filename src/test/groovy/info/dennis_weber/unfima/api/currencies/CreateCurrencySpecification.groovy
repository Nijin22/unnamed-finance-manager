package info.dennis_weber.unfima.api.currencies

import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification

class CreateCurrencySpecification extends AbstractUnfimaSpecification {
  def "Creating a new currency"() {
    given:
    Map request = ["shortName"           : "€",
                   "fullName"            : "Euro",
                   "fractionalName"      : "Cent",
                   "decimalPlaces"       : 2,
                   "starterRelativeValue": 1]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/currencies")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 201
    answer.class == Integer
  }

  def "Creating a new currency unauthenticated"() {
    given:
    Map request = ["shortName"           : "€",
                   "fullName"            : "Euro",
                   "fractionalName"      : "Cent",
                   "decimalPlaces"       : 2,
                   "starterRelativeValue": 1]

    when:
    setRequestBody(client, request)
    client.post("/v1.0/currencies")

    then:
    client.response.statusCode == 401
  }

  def "Creating a new currency without a body"() {
    when:
    authenticatedClient.post("/v1.0/currencies")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg == "Request body is required but missing"
  }

  def "Creating a new currency with an unneeded extra field"() {
    given:
    Map request = ["shortName"           : "€",
                   "fullName"            : "Euro",
                   "fractionalName"      : "Cent",
                   "decimalPlaces"       : 2,
                   "starterRelativeValue": 1,
                   "IAmUseless"          : "xyz"]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/currencies")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg.contains("Request body is not using the correct schema.")
  }

  def "Creating a new currency with an messed up body"() {
    given:
    String content = "yeah, that't not valid json."

    when:
    authenticatedClient.requestSpec({
      it.body({
        it.type("application/json")
        it.text(content)
      })
    })
    authenticatedClient.post("/v1.0/currencies")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg.contains("Request body is not using the correct schema.")
  }

  def "Creating a new currency with missing attributes"() {
    given:
    Map request = ["shortName"           : "€",
                   // Note that 'fullName' is missing
                   "fractionalName"      : "Cent",
                   "decimalPlaces"       : 2,
                   "starterRelativeValue": 1]

    when:
    setRequestBody(authenticatedClient, request)
    authenticatedClient.post("/v1.0/currencies")
    def answer = getResponseObject(authenticatedClient)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg == "Required parameter 'fullName' is missing."
    answer.errorId == "REQUIRE_PARAMETER_MISSING"
  }
}
