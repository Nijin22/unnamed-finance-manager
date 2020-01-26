package info.dennis_weber.unfima.api

import spock.lang.Specification

import javax.ws.rs.core.UriBuilder

abstract class UnfimaSpecification extends Specification {
    Application application

    private String TEST_URL_BASE = "http://localhost:8080"
    private List<HttpURLConnection> connections = []

    def setup() {
        final String databaseJdbcUrl = "jdbc:h2:~/unfimaInMemoryTestDb;MODE=MySQL;DATABASE_TO_LOWER=TRUE"
        final String databaseUsername = "sa"
        final String databasePassword = null

        application = new Application(new URI(TEST_URL_BASE), databaseJdbcUrl, databaseUsername, databasePassword, true)
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
