package info.dennis_weber.unfima.api.users

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.UnfimaServerBackedApplicationUnderTest
import ratpack.http.client.ReceivedResponse

class AuthenticateSpecification extends AbstractUnfimaSpecification {

  def "Authenticating an existing account"() {
    given:
    String email = UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.email
    String password = UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.password
    String clientIdentifier = "Spock Testrunner"
    Map request = ["email": email, "password": password, "client": clientIdentifier]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/authenticate")
    String token = new JsonSlurper().parseText(resp.body.text).get("bearerToken")

    then:
    resp.statusCode == 200
    token != null
  }

  def "Authenticating without email"() {
    given:
    String password = "doesNotMatter"
    String clientIdentifier = "Spock Testrunner"
    Map request = ["password": password, client: clientIdentifier]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/authenticate")

    then:
    resp.statusCode == 400
    resp.body.text.contains("required parameter 'email' is missing")
  }

  def "Authenticating without password"() {
    given:
    String email = UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.email
    String clientIdentifier = "Spock Testrunner"
    Map request = ["email": email, client: clientIdentifier]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/authenticate")

    then:
    resp.statusCode == 400
    resp.body.text.contains("required parameter 'password' is missing")
  }

  def "Authenticating without client"() {
    given:
    String email = UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.email
    String password = UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.password
    Map request = ["email": email, "password": password]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/authenticate")

    then:
    resp.statusCode == 400
    resp.body.text.contains("required parameter 'client' is missing")
  }

  def "Authenticating with a too long client identifier"() {
    given:
    String email = UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.email
    String password = UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.password
    String clientIdentifier = "This client identifier is way, way, way" +
        "way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, " +
        "way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, " +
        "too long."
    Map request = ["email": email, "password": password, "client": clientIdentifier]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/authenticate")

    then:
    resp.statusCode == 400
    resp.body.text.contains("'client' is ${clientIdentifier.size()} characters long, limit is 255.")
  }

  def "Authenticating with wrong email"() {
    given:
    String email = "does.not.have@an.account"
    String password = "doesNotMatter"
    String clientIdentifier = "Spock Testrunner"
    Map request = ["email": email, "password": password, "client": clientIdentifier]

    when:
    setRequestBody(client, request)
    client.post("v1.0/authenticate")
    def answer = getResponseObject(client)

    then:
    client.response.statusCode == 401
    answer.errorMsg == "Email address '$email' not found"
    answer.errorId == "USERNAME_UNKNOWN"
  }

  def "Authenticating with wrong password"() {
    given:
    String email = UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.email
    String password = "theWrongOne"
    String clientIdentifier = "Spock Testrunner"
    Map request = ["email": email, "password": password, "client": clientIdentifier]

    when:
    setRequestBody(client, request)
    client.post("v1.0/authenticate")
    def answer = getResponseObject(client)

    then:
    client.response.statusCode == 401
    answer.errorMsg == "Password doesn't match email '$email'"
    answer.errorId == "PASSWORD_DOES_NOT_MATCH"
  }
}
