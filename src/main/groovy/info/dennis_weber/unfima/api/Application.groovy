package info.dennis_weber.unfima.api

import info.dennis_weber.unfima.api.handlers.v1_0.ExceptionHandler
import info.dennis_weber.unfima.api.handlers.v1_0.currencies.CreateCurrencyHandler
import info.dennis_weber.unfima.api.handlers.v1_0.currencies.ListAllCurrenciesHandler
import info.dennis_weber.unfima.api.handlers.v1_0.currencies.ListSingleCurrencyHandler
import info.dennis_weber.unfima.api.handlers.v1_0.users.AuthenticateHandler
import info.dennis_weber.unfima.api.handlers.v1_0.users.BasicUserDetailsHandler
import info.dennis_weber.unfima.api.handlers.v1_0.users.RegisterAccountHandler
import info.dennis_weber.unfima.api.services.DatabaseService
import org.flywaydb.core.Flyway
import ratpack.error.ServerErrorHandler
import ratpack.guice.Guice
import ratpack.server.BaseDir
import ratpack.server.RatpackServer

import java.util.logging.Logger

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

    // Start Ratpack Server
    RatpackServer.start() { server ->
      server.serverConfig({ configBuilder ->
        configBuilder.baseDir(BaseDir.find(".ratpackBaseDirMarker"))
      })
      server.registry(Guice.registry({ b ->
        // handlers
        // I don't know why we have to bind handlers explicitly, according to the docs, Guice should be able
        // to do that just-in-time: https://ratpack.io/manual/current/api/ratpack/guice/Guice.html
        b.bind(RegisterAccountHandler)
        b.bind(AuthenticateHandler)
        b.bind(BasicUserDetailsHandler)
        b.bind(CreateCurrencyHandler)
        b.bind(ListSingleCurrencyHandler)
        b.bind(ListAllCurrenciesHandler)

        // services
        b.bindInstance(new DatabaseService(databaseJdbcUrl, databaseUsername, databasePassword))

        // custom error handler
        b.bind(ServerErrorHandler.class, ExceptionHandler.class)
      }))
      server.handlers() { chain ->
        chain.with {
          // static files
          files({ fileHandlerSpec ->
            fileHandlerSpec.dir("static").indexFiles("index.html")
          })

          // User accounts
          post("v1.0/users", RegisterAccountHandler) // Register new account
          post("v1.0/authenticate", AuthenticateHandler) // Authenticate and get token
          get("v1.0/users/me", BasicUserDetailsHandler) // Basic user details

          // Currencies
          prefix("v1.0/currencies", { c ->
            c.path("") { ctx ->
              ctx.byMethod() { methodSpec ->
                methodSpec.post(CreateCurrencyHandler)
                methodSpec.get(ListAllCurrenciesHandler)
              }
            }
            c.get(":currencyId", ListSingleCurrencyHandler)
          })

          get("v1.0/err", { throw new RuntimeException("woops.") })
        }
      }
    }
    isRunning = true

  }
}
