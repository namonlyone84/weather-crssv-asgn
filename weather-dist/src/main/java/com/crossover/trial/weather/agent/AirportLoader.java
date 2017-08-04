package com.crossover.trial.weather.agent;

import com.crossover.trial.weather.client.AirportCSVParser;
import com.crossover.trial.weather.client.RestClient;
import com.crossover.trial.weather.entity.Airport;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 *
 * @author code test administrator
 */
public class AirportLoader {
    private static final String BASE_URI = "http://localhost:9090";

    private RestClient client;

    private AirportCSVParser csvParser;

    public AirportLoader() {
        client = new RestClient(BASE_URI);
        csvParser = new AirportCSVParser();
    }

    private void upload(List<Airport> airports) throws IOException {
        airports.stream().forEach(airport -> {
            Response response = client.addAirport(airport.getIata(), airport.getLatitude(), airport.getLongitude());
            System.out.println(String.format("Upload airport %1$s : %2$s", airport, response.readEntity(String.class)));
        });
    }

    public void runForFile(String filePath) throws IOException {
        List<Airport> airports = csvParser.readAirportFromFile(filePath);
        upload(airports);
    }

    public static void main(String args[]) throws IOException {
        String filePath = null;

        if (args != null && args.length >= 1) {
            File airportDataFile = new File(args[0]);

            if (!airportDataFile.exists() || airportDataFile.length() == 0) {
                System.err.println(airportDataFile + " is not a valid input");
                System.exit(1);
            }

            filePath = args[0];
        }

        AirportLoader airportLoader = new AirportLoader();
        airportLoader.runForFile(filePath);
    }
}
