package info.dennis_weber.unfima.api.currencies

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.UnfimaServerBackedApplicationUnderTest

class UpdateCurrencySpecification extends AbstractUnfimaSpecification {
  def "Updating the shortName of a currency"() {
    given:
    String newShortName = "NewShortName"
    int id = UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.id
    String json = JsonOutput.toJson(["shortName": newShortName])

    when:
    authenticatedClient.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
    authenticatedClient.put("/v1.0/currencies/" + id)
    def answerToUpdateCall = authenticatedClient.response.body.text
    def statusToUpdateCall = authenticatedClient.response.statusCode
    // Then get the updated currency
    authenticatedClient.get("/v1.0/currencies/" + id)
    def updatedCurrency = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    statusToUpdateCall == 204
    answerToUpdateCall.empty
    updatedCurrency.shortName == newShortName
  }

  def "Updating all attributes of a currency"() {
    given:
    String newShortName = "XBT"
    String newFullName = "Bitcoin"
    String newFractionalName = "Satoshi"
    int newDecimalPlaces = 8
    int id = UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.id
    String json = JsonOutput.toJson([
        "shortName"     : newShortName,
        "fullName"      : newFullName,
        "fractionalName": newFractionalName,
        "decimalPlaces" : newDecimalPlaces
    ])

    when:
    authenticatedClient.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
    authenticatedClient.put("/v1.0/currencies/" + id)
    def answerToUpdateCall = authenticatedClient.response.body.text
    def statusToUpdateCall = authenticatedClient.response.statusCode
    // Then get the updated currency
    authenticatedClient.get("/v1.0/currencies/" + id)
    def updatedCurrency = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    statusToUpdateCall == 204
    answerToUpdateCall.empty
    updatedCurrency.shortName == newShortName
    updatedCurrency.fullName == newFullName
    updatedCurrency.fractionalName == newFractionalName
    updatedCurrency.decimalPlaces == newDecimalPlaces
  }

  def "Updating NO attributes of a currency (but still calling the end point)"() {
    given:
    int id = UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.id
    String json = JsonOutput.toJson([])

    when:
    authenticatedClient.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
    authenticatedClient.put("/v1.0/currencies/" + id)
    def answerToUpdateCall = authenticatedClient.response.body.text
    def statusToUpdateCall = authenticatedClient.response.statusCode
    // Then get the "updated" currency
    authenticatedClient.get("/v1.0/currencies/" + id)
    def updatedCurrency = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    statusToUpdateCall == 204
    answerToUpdateCall.empty
    // all values stay the same
    updatedCurrency.id == UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.id
    updatedCurrency.shortName == UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.shortName
    updatedCurrency.fullName == UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.fullName
    updatedCurrency.fractionalName == UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.fractionalName
    updatedCurrency.decimalPlaces == UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.decimalPlaces
  }

  def "Updating the ID of a currency"() {
    given:
    int id = UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.id
    int desiredNewId = 987
    String json = JsonOutput.toJson(["id": desiredNewId])

    when:
    authenticatedClient.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
    authenticatedClient.put("/v1.0/currencies/" + id)
    def answerToUpdateCall = new JsonSlurper().parseText(authenticatedClient.response.body.text)
    def statusToUpdateCall = authenticatedClient.response.statusCode
    // Then get the "updated" currency
    authenticatedClient.get("/v1.0/currencies/" + id)
    def updatedCurrency = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    statusToUpdateCall == 400
    answerToUpdateCall.errorMsg == "You requested to change the currency's ID from '$id' to '$desiredNewId', but changing IDs is not possible."
    // all values stay the same
    updatedCurrency.id == UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.id
    updatedCurrency.shortName == UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.shortName
    updatedCurrency.fullName == UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.fullName
    updatedCurrency.fractionalName == UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.fractionalName
    updatedCurrency.decimalPlaces == UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.decimalPlaces
  }

  def "Updating a currency that does not exist"() {
    given:
    int id = 987654321 // does not exist
    String newShortName = "doesNotMatter"
    String json = JsonOutput.toJson(["shortName": newShortName])

    when:
    authenticatedClient.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
    authenticatedClient.put("/v1.0/currencies/" + id)

    then:
    authenticatedClient.response.statusCode == 404
  }

  def "Updating a currency with a broken request body"() {
    given:
    int id = UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.id
    String json = "this isn't actually json"

    when:
    authenticatedClient.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
    authenticatedClient.put("/v1.0/currencies/" + id)

    then:
    authenticatedClient.response.statusCode == 400
  }

  def "Updating a currency but missing the body"() {
    given:
    int id = UnfimaServerBackedApplicationUnderTest.TEST_DATA.currency.id

    when:
    authenticatedClient.put("/v1.0/currencies/" + id)
    def answer = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    authenticatedClient.response.statusCode == 400
    answer.errorMsg == "Request body is required but missing"
  }
}
