package info.dennis_weber.unfima.api.helpers

import info.dennis_weber.unfima.api.Application
import ratpack.server.RatpackServer
import ratpack.test.ServerBackedApplicationUnderTest

import static ratpack.server.internal.ServerCapturer.capture

class UnfimaServerBackedApplicationUnderTest extends ServerBackedApplicationUnderTest {
    @Override
    protected RatpackServer createServer() throws Exception {
        // Just as MainClassApplicationUnderTest.createServer() does, we also use the "ServerCapturer" to capture
        // the RatpackServer we need to return.
        return capture({
            // Start the application with a test database
            final String databaseJdbcUrl = "jdbc:h2:mem:unfimaInMemoryTestDb;MODE=MySQL;DATABASE_TO_LOWER=TRUE"
            final String databaseUsername = "sa"
            final String databasePassword = null

            Application application = new Application(databaseJdbcUrl, databaseUsername, databasePassword)
            application.cleanupDatabase()
            application.startServer()
        })
    }
}
