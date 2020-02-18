package info.dennis_weber.unfima.api.helpers

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import ratpack.test.ApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.Specification

abstract class AbstractUnfimaSpecification extends Specification {
  TestHttpClient client
  TestHttpClient authenticatedClient

  final private JsonSlurper slurper = new JsonSlurper()

  def setup() {
    // Start the application with a test environment
    ApplicationUnderTest aut = new UnfimaServerBackedApplicationUnderTest()

    // Create a client
    client = TestHttpClient.testHttpClient(aut)

    // Create a client for endpoints which require authentication
    authenticatedClient = TestHttpClient.testHttpClient(aut, { reqSpec ->
      reqSpec.headers({ headers ->
        headers.set("Authorization", "Bearer ${TestDataProvider.TEST_DATA.user.token}".toString())
      })
    })

    // Many tests require data from the UnfimaServerBackedApplicationUnderTest.TEST_DATA map.
    // Some of this data (database-generated IDs) is only available after a initial HTTP call has been sent.
    // So we do this initial call here:
    client.get() // the initial call to the root page
  }

  def getResponseObject(TestHttpClient client) {
    return slurper.parseText(client.response.body.text)
  }

  /**
   * Sets the request body to the specified value in a json format
   *
   * @param client
   * @param body
   * @return
   */
  void setRequestBody(TestHttpClient client, Map body) {
    client.requestSpec({
      it.body({
        it.type("application/json")
        it.text(JsonOutput.toJson(body))
      })
    })
  }
}
