package info.dennis_weber.unfima.api.helpers

import ratpack.test.ApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.Specification

abstract class AbstractUnfimaSpecification extends Specification {
  TestHttpClient client
  TestHttpClient authenticatedClient

  def setup() {
    // Start the application with a test environment
    ApplicationUnderTest aut = new UnfimaServerBackedApplicationUnderTest()

    // Create a client
    client = TestHttpClient.testHttpClient(aut)

    // Create a client for endpoints which require authentication
    authenticatedClient = TestHttpClient.testHttpClient(aut, { reqSpec ->
      reqSpec.headers({ headers ->
        headers.set("Authorization", "Bearer ${UnfimaServerBackedApplicationUnderTest.TEST_DATA.user.token}".toString())
      })
    })
  }
}
