package info.dennis_weber.unfima.api.helpers

import ratpack.test.ApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.Specification

abstract class AbstractUnfimaSpecification extends Specification {
    TestHttpClient client

    def setup() {
        // Create a client
        ApplicationUnderTest aut = new UnfimaServerBackedApplicationUnderTest()
        client = TestHttpClient.testHttpClient(aut)
    }
}
