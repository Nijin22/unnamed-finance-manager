package info.dennis_weber.unfima.api;

import info.dennis_weber.unfima.api.resources.v1_0.RootResource;
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

    public static void main(String[] args) {

        // Set base URI
        URI baseUri;
        try {
            baseUri = new URI("http://localhost:8080");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Application mainApp = new Application(baseUri);
        mainApp.startServer();

        System.out.println("Application started. Stop the application using CTRL+C");
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Application(URI baseUri) {
        this.baseUri = baseUri;
        logger = Logger.getLogger(Application.class.getName());
    }

    public void startServer() {
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
