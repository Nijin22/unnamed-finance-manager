package info.dennis_weber.unfima.api

import groovy.json.JsonSlurper
import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification

class UnexpectedErrorSpecification extends AbstractUnfimaSpecification {
  def "Causing a unexpected runtime exception"() {
    // there really isn't something like a "testable unexpected runtime exception", as if it is testable, it will no
    // longer be unexpected. But we can simulate that with the hidden runtimeExceptionSimulation endpoint

    when:
    client.get("v1.0/runtimeExceptionSimulation")
    def answer = getResponseObject(client)

    then:
    client.response.statusCode == 500
    answer.info.contains("A unexpected error occurred. Ideally, this should never happen.")
    answer.errorMsg == "This is a simulated runtime exception. Nothing actually broke."
    answer.errorClass == "java.lang.RuntimeException"
  }
}
