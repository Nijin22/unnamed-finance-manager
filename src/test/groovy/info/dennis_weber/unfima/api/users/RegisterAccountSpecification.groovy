package info.dennis_weber.unfima.api.users

import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.UnfimaServerBackedApplicationUnderTest
import info.dennis_weber.unfima.api.services.CurrencyDto
import ratpack.http.client.ReceivedResponse

class RegisterAccountSpecification extends AbstractUnfimaSpecification {
  def "Registering a new account"() {
    given:
    String email = "a.new.user@to.add"
    String password = "password-in-tests"
    CurrencyDto starterCurrency = new CurrencyDto()
    starterCurrency.shortName = "EUR"
    starterCurrency.fullName = "Euro"
    starterCurrency.fractionalName = "Cent"
    starterCurrency.decimalPlaces = 2
    Map request = ["email": email, "password": password, "starterCurrency": starterCurrency]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 201
  }

  def "Registering an email that already exists"() {
    given:
    String email = UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.email
    String password = "doesNotMatter"
    CurrencyDto starterCurrency = new CurrencyDto()
    starterCurrency.shortName = "doesNotMatter"
    starterCurrency.fullName = "doesNotMatter"
    starterCurrency.fractionalName = "doesNotMatter"
    starterCurrency.decimalPlaces = 2
    Map request = ["email": email, "password": password, "starterCurrency": starterCurrency]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 409
    resp.body.text.contains("email address is already in use")
  }

  def "Registering without email"() {
    given:
    String password = "doesNotMatter"
    Map request = ["password": password]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 400
    resp.body.text.contains("required parameter 'email' is missing")
  }

  def "Registering with too long email"() {
    given:
    String email = "a.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way" +
        ".way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way" +
        ".way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way.way" +
        ".way.way.way.way.way.way.way.way.way.way.way.way@to.long.email.address"
    String password = "doesNotMatter"
    Map request = ["email": email, "password": password]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 400
    resp.body.text.contains("'email' parameter is too long")
  }

  def "Registering without password"() {
    given:
    String email = "a.new.user@to.add"
    Map request = ["email": email]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 400
    resp.body.text.contains("required parameter 'password' is missing")
  }

  def "Registering with a too long password"() {
    given:
    String email = "a.new.user@to.add"
    String password = "this is way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way," +
        " way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way," +
        " too long, even for a reasonable secure password, as it would allow DOS by taking too much CPU time to bcrypt!"
    Map request = ["email": email, "password": password]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 400
    resp.body.text.contains("'password' parameter is too long")
  }

  def "Registering without starterCurrency"() {
    given:
    String email = "a.new.user@to.add"
    String password = "password-in-tests"
    Map request = ["email": email, "password": password]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 400
  }

  def "Registering with a starterCurrency that misses something"() {
    String email = UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.email
    String password = "doesNotMatter"
    CurrencyDto starterCurrency = new CurrencyDto()
    // note that the short name is missing
    starterCurrency.fullName = "doesNotMatter"
    starterCurrency.fractionalName = "doesNotMatter"
    starterCurrency.decimalPlaces = 2
    Map request = ["email": email, "password": password, "starterCurrency": starterCurrency]

    when:
    setRequestBody(client, request)
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 400
  }

  def "Registering a new account, but forgetting the request body"() {
    when:
    ReceivedResponse resp = client.post("v1.0/users")
    def answer = getResponseObject(client)

    then:
    resp.statusCode == 400
    answer.errorMsg == "Request body is required but missing"
  }
}
