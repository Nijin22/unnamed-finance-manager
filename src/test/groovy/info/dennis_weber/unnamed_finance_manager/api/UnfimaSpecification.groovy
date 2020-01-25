package info.dennis_weber.unnamed_finance_manager.api

import spock.lang.Specification

import javax.ws.rs.core.UriBuilder

abstract class UnfimaSpecification extends Specification {
    Application application

    private String TEST_URL_BASE = "http://localhost:8080"
    private List<HttpURLConnection> connections = []

    def setup() {
        application = new Application(new URI(TEST_URL_BASE))
        application.startServer()
    }

    def cleanup() {
        connections.each {
            it.disconnect()
        }

        application.stopServer()
    }

    HttpURLConnection getConnection(String path) {
        URL url = UriBuilder.fromUri(TEST_URL_BASE + path).build().toURL()
        HttpURLConnection connection = url.openConnection() as HttpURLConnection

        connections.add(connection)

        return connection
    }

}
