package info.dennis_weber.unfima.api.helpers

import groovy.sql.Sql
import info.dennis_weber.unfima.api.Application
import ratpack.server.RatpackServer
import ratpack.test.ServerBackedApplicationUnderTest

import static ratpack.server.internal.ServerCapturer.capture

class UnfimaServerBackedApplicationUnderTest extends ServerBackedApplicationUnderTest {
  // Config for in-memory h2 database
  final String databaseJdbcUrl = "jdbc:h2:mem:unfimaTestDb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
  final String databaseUsername = "sa"
  final String databasePassword = null


  @Override
  protected RatpackServer createServer() throws Exception {
    // Just as MainClassApplicationUnderTest.createServer() does, we also use the "ServerCapturer" to capture
    // the RatpackServer we need to return.
    return capture({

      // Setup the application with the test db
      Application application = new Application(databaseJdbcUrl, databaseUsername, databasePassword)

      // Ensure a clean new database is used so all tests are independent of each other
      application.cleanupDatabase()

      // Start the application
      application.startServer()

      // Fill the application with test data
      Sql sql = Sql.newInstance(databaseJdbcUrl, databaseUsername, databasePassword)
      TestDataProvider testDataProvider = new TestDataProvider(sql)
      testDataProvider.fillWithTestData()
    })
  }
}
