package info.dennis_weber.unfima.api;

import info.dennis_weber.unfima.api.resources.v1_0.RootResource;
import org.flywaydb.core.Flyway;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

class Application {
    private URI baseUri;
    private HttpServer server;
    private Logger logger;

    // database config
    private String databaseJdbcUrl;
    private String databaseUsername;
    private String databasePassword;
    private boolean databaseCleanOnStartup;

    public static void main(String[] args) {
        // Set base URI
        URI baseUri;
        try {
            baseUri = new URI("http://localhost:8080");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Read db connection config from env variables
        String databaseJdbcUrl = System.getenv("UNFIMA_DATABASE_JDBC_URL");
        if (databaseJdbcUrl == null) {
            throw new RuntimeException("Required environmental variable 'UNFIMA_DATABASE_JDBC_URL' is not set.");
        }
        String databaseUsername = System.getenv("UNFIMA_DATABASE_USERNAME");
        if (databaseUsername == null) {
            throw new RuntimeException("Required environmental variable 'UNFIMA_DATABASE_USERNAME' is not set.");
        }
        String databasePassword = System.getenv("UNFIMA_DATABASE_PASSWORD");
        if (databasePassword == null) {
            throw new RuntimeException("Required environmental variable 'UNFIMA_DATABASE_PASSWORD' is not set.");
        }
        boolean cleanOnStartup = false;
        if ("true".equals(System.getenv("UNFIMA_DATABASE_CLEAN_ON_START"))) {
            cleanOnStartup = true;
        }

        Application mainApp = new Application(baseUri, databaseJdbcUrl, databaseUsername, databasePassword, cleanOnStartup);
        mainApp.startServer();

        System.out.println("Application started. Stop the application using CTRL+C");
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new instance of the Unfima application which can then be started by calling {@link #startServer()}.
     *
     * @param baseUri                application url, including port to use. e.g. "http://localhost:8080"
     * @param databaseJdbcUrl        database connection url. e.g. "jdbc:mariadb://localhost:3306/unfima"
     * @param databaseUsername       database username
     * @param databasePassword       database password
     * @param databaseCleanOnStartup set to true if you want to remove all data from the database on startup (useful for tests)
     */
    Application(URI baseUri, String databaseJdbcUrl, String databaseUsername, String databasePassword, boolean databaseCleanOnStartup) {
        this.baseUri = baseUri;
        this.databaseJdbcUrl = databaseJdbcUrl;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        this.databaseCleanOnStartup = databaseCleanOnStartup;

        logger = Logger.getLogger(Application.class.getName());
    }

    public void startServer() {
        // Setup database
        Flyway flyway = Flyway.configure().dataSource(databaseJdbcUrl, databaseUsername, databasePassword).load();
        if (databaseCleanOnStartup) {
            flyway.clean();
        }
        flyway.migrate();

        // Start Grizzly HTTP Server
        try {
            final ResourceConfig resourceConfig = new ResourceConfig(RootResource.class);
            server = GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig, false);
            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
            server.start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    public void stopServer() {
        server.shutdownNow();
    }
}
