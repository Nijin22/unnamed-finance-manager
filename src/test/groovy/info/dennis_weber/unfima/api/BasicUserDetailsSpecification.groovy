package info.dennis_weber.unfima.api

import groovy.json.JsonSlurper
import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.UnfimaServerBackedApplicationUnderTest

/**
 * Tests the basic user details endpoint, but also the AbstractAuthenticatedUnfimaHandler
 */
class BasicUserDetailsSpecification extends AbstractUnfimaSpecification {
  def "Getting basic user details for a valid user"() {
    when:
    authenticatedClient.get("/v1.0/users/me")
    def answer = new JsonSlurper().parseText(authenticatedClient.response.body.text)

    then:
    authenticatedClient.response.statusCode == 200
    answer.id == UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.id
    answer.email == UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.email
  }

  def "Getting basic user details without a token"() {
    when:
    client.get("/v1.0/users/me")
    def answer = new JsonSlurper().parseText(client.response.body.text)

    then:
    client.response.statusCode == 401
    answer.errorMsg == "Failed to extract Authorization token."
    answer.errorId == "NO_TOKEN_PROVIDED"
  }

  def "Getting basic user details with an invalid token"() {
    given:
    String token = "ATokenIJustDidNotGetFromTheServer"

    when:
    client.requestSpec({ reqSpec ->
      reqSpec.headers({ headers ->
        headers.set("Authorization", "Bearer $token")
      })
    })
    client.get("/v1.0/users/me")
    def answer = new JsonSlurper().parseText(client.response.body.text)

    then:
    client.response.statusCode == 401
    answer.errorMsg == "Token '$token' is invalid."
    answer.errorId == "TOKEN_NOT_VALID"
  }

  def "Getting basic user details with an f*****-up Authorization header"() {
    given:
    String authHeader = "OhBoy,IForgotThatThisHeaderHasToStartWithBearerAndASpace"

    when:
    client.requestSpec({ reqSpec ->
      reqSpec.headers({ headers ->
        headers.set("Authorization", authHeader)
      })
    })
    client.get("/v1.0/users/me")
    def answer = new JsonSlurper().parseText(client.response.body.text)

    then:
    client.response.statusCode == 401
    answer.errorMsg == "Failed to extract Authorization token."
    answer.errorId == "NO_TOKEN_PROVIDED"
  }
}
