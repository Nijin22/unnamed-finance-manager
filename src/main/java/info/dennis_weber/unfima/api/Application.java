package info.dennis_weber.unfima.api;

import org.flywaydb.core.Flyway;
import ratpack.server.RatpackServer;

import java.util.logging.Logger;

public class Application {
    private Logger logger;
    private Flyway flyway;

    // database config
    private String databaseJdbcUrl;
    private String databaseUsername;
    private String databasePassword;

    /**
     * Creates a new instance of the Unfima application which can then be started by calling {@link #startServer()}.
     *
     * @param databaseJdbcUrl        database connection url. e.g. "jdbc:mariadb://localhost:3306/unfima"
     * @param databaseUsername       database username
     * @param databasePassword       database password
     */
    Application(String databaseJdbcUrl, String databaseUsername, String databasePassword) {
        this.databaseJdbcUrl = databaseJdbcUrl;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;

        logger = Logger.getLogger(Application.class.getName());
        flyway = Flyway.configure().dataSource(databaseJdbcUrl, databaseUsername, databasePassword).load();
    }

    public void cleanupDatabase() {
        flyway.clean();
        // TODO: Throw exception when called while app is already running
        // TODO: Javadoc
    }

    public void startServer() throws Exception {
        // Setup database
        flyway.migrate();

        // Start Ratpack Server
        RatpackServer.start(server -> server
                .handlers(chain -> chain
                        .get(ctx -> ctx.render("Hello World!"))
                        .get(":name", ctx -> ctx.render("Hello " + ctx.getPathTokens().get("name") + "!"))
                )
        );

    }
}
