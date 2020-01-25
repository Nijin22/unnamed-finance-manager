package info.dennis_weber.unnamed_finance_manager.api;

import info.dennis_weber.unnamed_finance_manager.api.resources.v1_0.RootResource;
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
    private Logger logger;

    public static void main(String[] args) {
        try {
            new Application(new URI("http://localhost:8080")).startServer();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    Application(URI baseUri) {
        this.baseUri = baseUri;
        logger = Logger.getLogger(Application.class.getName());
    }

    private void startServer() {
        try {
            final ResourceConfig resourceConfig = new ResourceConfig(RootResource.class);
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig, false);
            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
            server.start();

            logger.info("Application started. Stop the application using CTRL+C");
            Thread.currentThread().join();

        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }
}
