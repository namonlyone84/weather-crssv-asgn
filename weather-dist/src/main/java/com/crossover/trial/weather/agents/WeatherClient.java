package com.crossover.trial.weather.agents;

import com.crossover.trial.weather.client.RestClient;
import com.crossover.trial.weather.entities.DataPoint;

import javax.ws.rs.core.Response;

/**
 * A reference implementation for the checkWeatherForAirport client. Consumers of the REST API can look at WeatherClient
 * to understand API semantics. This existing client populates the REST endpoint with dummy data useful for
 * testing.
 *
 * @author code test administrator
 */
public class WeatherClient {
    private static final String BASE_URI = "http://localhost:9090";

    private RestClient client;

    public WeatherClient() {
        client = new RestClient(BASE_URI);
    }

    public void pingCollect() {
        Response response = client.pingCollect();
        System.out.print("collect.ping: " + response.readEntity(String.class) + "\n");

    }

    public void query(String iata, double radius) {
        Response response = client.query(iata, radius);
        System.out.println("query." + iata + "." + radius + ": " + response.readEntity(String.class));
    }

    public void populate(String pointType, int first, int last, int mean, int median, int count) {
        DataPoint dataPoint = new DataPoint.Builder()
                .withFirst(first)
                .withLast(last)
                .withMean(mean)
                .withMedian(median)
                .withCount(count)
                .build();
        Response post = client.addWeather("BOS", pointType, dataPoint);
        System.out.println("collect.weather: " + post.readEntity(String.class));
    }

    public void pingQuery() {
        Response response = client.pingQuery();
        System.out.println("query.ping: " + response.readEntity(String.class));
    }

    public void exit() {
        client.exit();
    }

    public static void main(String[] args) {
        WeatherClient weatherClient = new WeatherClient();

        weatherClient.pingCollect();
        weatherClient.populate("wind", 0, 10, 6, 4, 20);

        double weatherRadius = 0;
        weatherClient.query("BOS", weatherRadius);
        weatherClient.query("JFK", weatherRadius);
        weatherClient.query("EWR", weatherRadius);
        weatherClient.query("LGA", weatherRadius);
        weatherClient.query("MMU", weatherRadius);

        weatherClient.pingQuery();
        weatherClient.exit();
        System.out.print("complete");
        System.exit(0);
    }
}
