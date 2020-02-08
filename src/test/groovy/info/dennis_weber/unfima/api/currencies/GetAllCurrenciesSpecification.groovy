package info.dennis_weber.unfima.api.currencies

import groovy.json.JsonSlurper
import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification

class GetAllCurrenciesSpecification extends AbstractUnfimaSpecification {
  def "Getting details for all currencies"() {
    when:
    authenticatedClient.get("/v1.0/currencies")
    def answer = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    answer instanceof List
    answer.size == 1 // In the tests, there is only one currency
  }
}
