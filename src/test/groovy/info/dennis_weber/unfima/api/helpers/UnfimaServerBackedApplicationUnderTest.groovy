package info.dennis_weber.unfima.api.helpers

import info.dennis_weber.unfima.api.Application
import org.h2.tools.Server
import ratpack.server.RatpackServer
import ratpack.test.ServerBackedApplicationUnderTest

import static ratpack.server.internal.ServerCapturer.capture

class UnfimaServerBackedApplicationUnderTest extends ServerBackedApplicationUnderTest {
  @Override
  protected RatpackServer createServer() throws Exception {
    // Just as MainClassApplicationUnderTest.createServer() does, we also use the "ServerCapturer" to capture
    // the RatpackServer we need to return.
    return capture({
      // Config for in-memory h2 database
      final String databaseJdbcUrl = "jdbc:h2:mem:unfimaTestDb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
      final String databaseUsername = "sa"
      final String databasePassword = null

      // Setup the application with the test db
      Application application = new Application(databaseJdbcUrl, databaseUsername, databasePassword)
      application.cleanupDatabase()
      application.startServer()
    })
  }
}
