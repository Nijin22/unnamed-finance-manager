package info.dennis_weber.unfima.api

import groovy.json.JsonOutput
import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification
import info.dennis_weber.unfima.api.helpers.UnfimaServerBackedApplicationUnderTest
import ratpack.func.Action
import ratpack.http.client.ReceivedResponse
import ratpack.http.client.RequestSpec

class RegisterAccountSpecification extends AbstractUnfimaSpecification {
  def "registering a new account"() {
    given:
    String email = "a.new.user@to.add"
    String password = "password-in-tests"

    when:
    String json = JsonOutput.toJson(["email": email, "password": password])
    client.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 201
  }

  def "Registering an email that already exists"() {
    given:
    String email = UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.email
    String password = "doesNotMatter"

    when:
    String json = JsonOutput.toJson(["email": email, "password": password])
    client.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 409
    resp.body.text.contains("email address is already in use")
  }

  def "Registering without email"() {
    given:
    String password = "doesNotMatter"

    when:
    String json = JsonOutput.toJson(["password": password])
    client.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
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

    when:
    String json = JsonOutput.toJson(["email": email, "password": password])
    client.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 400
    resp.body.text.contains("'email' parameter is too long")
  }

  def "Registering without password"() {
    given:
    String email = "a.new.user@to.add"

    when:
    String json = JsonOutput.toJson(["email" : email])
    client.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 400
    resp.body.text.contains("required parameter 'password' is missing")
  }

  def "registering with a too long password"() {
    given:
    String email = "a.new.user@to.add"
    String password = "this is way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way," +
        " way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way, way," +
        " too long, even for a reasonable secure password, as it would allow DOS by taking too much CPU time to bcrypt!"

    when:
    String json = JsonOutput.toJson(["email": email, "password": password])
    client.requestSpec({
      it.body({
        it.type("application/json")
        it.text(json)
      })
    })
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 400
    resp.body.text.contains("'password' parameter is too long")
  }

}
