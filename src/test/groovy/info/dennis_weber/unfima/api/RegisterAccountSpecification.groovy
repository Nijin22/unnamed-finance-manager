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
    client.requestSpec(new Action<RequestSpec>() {
      @Override
      void execute(RequestSpec requestSpec) throws Exception {
        requestSpec.body(new Action<RequestSpec.Body>() {
          @Override
          void execute(RequestSpec.Body body) throws Exception {
            body.type("application/json")
            body.text(json)
          }
        })
      }
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
    client.requestSpec(new Action<RequestSpec>() {
      @Override
      void execute(RequestSpec requestSpec) throws Exception {
        requestSpec.body(new Action<RequestSpec.Body>() {
          @Override
          void execute(RequestSpec.Body body) throws Exception {
            body.type("application/json")
            body.text(json)
          }
        })
      }
    })
    ReceivedResponse resp = client.post("v1.0/users")

    then:
    resp.statusCode == 409
  }

  // TODO: Tests with bad request syntax


}
