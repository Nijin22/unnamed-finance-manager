package info.dennis_weber.unfima.api;

/**
 * Used to start the Unfima App from somewhere NOT in a test
 */
public class Main {
    public static void main(String... args) throws Exception {
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

        Application mainApp = new Application(databaseJdbcUrl, databaseUsername, databasePassword);

        if (cleanOnStartup) {
            mainApp.cleanupDatabase();
        }

        mainApp.startServer();
    }
}
