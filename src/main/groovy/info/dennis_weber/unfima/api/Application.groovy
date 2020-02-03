package info.dennis_weber.unfima.api

import com.google.inject.AbstractModule
import com.google.inject.Injector
import info.dennis_weber.unfima.api.handlers.v1_0.users.AuthenticateHandler
import info.dennis_weber.unfima.api.handlers.v1_0.users.BasicUserDetailsHandler
import info.dennis_weber.unfima.api.handlers.v1_0.users.RegisterAccountHandler
import info.dennis_weber.unfima.api.services.DatabaseService
import org.flywaydb.core.Flyway
import ratpack.server.BaseDir
import ratpack.server.RatpackServer

import java.util.logging.Logger

import static com.google.inject.Guice.createInjector

class Application {
  private Logger logger
  private Flyway flyway
  private boolean isRunning = false

  // database config
  private String databaseJdbcUrl
  private String databaseUsername
  private String databasePassword

  /**
   * Creates a new instance of the Unfima application which can then be started by calling {@link #startServer()}.
   *
   * @param databaseJdbcUrl database connection url. e.g. "jdbc:mariadb://localhost:3306/unfima"
   * @param databaseUsername database username
   * @param databasePassword database password
   */
  Application(String databaseJdbcUrl, String databaseUsername, String databasePassword) {
    this.databaseJdbcUrl = databaseJdbcUrl
    this.databaseUsername = databaseUsername
    this.databasePassword = databasePassword

    logger = Logger.getLogger(Application.class.getName())
    flyway = Flyway.configure().dataSource(databaseJdbcUrl, databaseUsername, databasePassword).load()
  }

  /**
   * Removes all data and structure from the connected database so it can be re-created.
   *
   * @throws IllegalStateException if the application is already running
   */
  void cleanupDatabase() throws IllegalStateException {
    if (isRunning) {
      throw new IllegalStateException("Can't clean database for a already running server.")
    }
    flyway.clean()
  }

  void startServer() throws Exception {
    if (isRunning) {
      throw new IllegalStateException("Can't start a already running application.")
    }

    // Setup database
    flyway.migrate()

    // Setup services via Guice
    Injector injector = createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(DatabaseService).toInstance(new DatabaseService(databaseJdbcUrl, databaseUsername, databasePassword))
      }
    })

    // Start Ratpack Server
    RatpackServer.start() { server ->
      server.serverConfig({ configBuilder ->
        configBuilder.baseDir(BaseDir.find(".ratpackBaseDirMarker"))
      })
      server.handlers() { chain ->
        chain.with {
          // static files
          files({fileHandlerSpec ->
            fileHandlerSpec.dir("static").indexFiles("index.html")
          })

          // User accounts
          post("v1.0/users", injector.getInstance(RegisterAccountHandler)) // Register new account
          post("v1.0/authenticate", injector.getInstance(AuthenticateHandler)) // Authenticate and get token
          get("v1.0/users/me", injector.getInstance(BasicUserDetailsHandler)) // Basic user details
        }
      }
    }
    isRunning = true

  }
}
