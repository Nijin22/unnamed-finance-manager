package info.dennis_weber.unfima.api

import org.flywaydb.core.Flyway
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
            server.handlers() { chain ->
                chain.get("") { ctx ->
                    ctx.render("Hello World")
                }
            }
        }

        isRunning = true

    }
}
