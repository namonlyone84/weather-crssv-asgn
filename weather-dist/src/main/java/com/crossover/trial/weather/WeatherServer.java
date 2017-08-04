package com.crossover.trial.weather;

import com.crossover.trial.weather.configuration.AppConfig;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.HttpServerFilter;
import org.glassfish.grizzly.http.server.HttpServerProbe;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.crossover.trial.weather.configuration.AppConfig.SERVER_STOP;
import static java.lang.String.format;


/**
 * This main method will be use by the automated functional grader. You shouldn't move this class or remove the
 * main method. You may change the implementation, but we encourage caution.
 *
 * @author code test administrator
 */
public class WeatherServer {

    private static final String BASE_URL = "http://localhost:9090/";

    public static void main(String[] args) {
        try {
            System.out.println("Starting Weather App local testing server: " + BASE_URL);

            final ResourceConfig resourceConfig = new AppConfig();

            HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URL), resourceConfig, false);
            HttpServerProbe probe = new HttpServerProbe.Adapter() {
                public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection, Request request) {
                    System.out.println(request.getRequestURI());
                }
            };
            httpServer.getServerConfiguration().getMonitoringConfig().getWebServerConfig().addProbes(probe);


            // the autograder waits for this output before running automated tests, please don't remove it
            httpServer.start();
            System.out.println(format("Weather Server started.\n url=%s\n", BASE_URL));

            // blocks until the process is terminated
            waitToStopServer(httpServer);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(WeatherServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void waitToStopServer(HttpServer httpServer) throws InterruptedException {
        while (!SERVER_STOP) {
            Thread.sleep(500);
        }
        httpServer.shutdown();
    }
}
