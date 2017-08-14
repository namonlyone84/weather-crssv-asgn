package com.crossover.trial.weather.client;

import com.crossover.trial.weather.entity.DataPoint;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestClient {
    private static final String DEFAULT_BASE_URI = "http://localhost:9090";
    /**
     * end point for read queries
     */
    private WebTarget query;

    /**
     * end point to supply updates
     */
    private WebTarget collect;

    public RestClient(String baseUri) {
        if (StringUtils.isBlank(baseUri)) {
            baseUri = DEFAULT_BASE_URI;
        }

        Client client = ClientBuilder.newClient();
        query = client.target(baseUri + "/query");
        collect = client.target(baseUri + "/collect");
    }

    public Response pingCollect() {
        WebTarget path = collect.path("/ping");
        return path.request().get();
    }

    public Response query(String iata, double radius) {
        WebTarget path = query.path("/weather/" + iata + "/" + radius);
        Response response = path.request().get();
        return response;
    }

    public Response pingQuery() {
        WebTarget path = query.path("/ping");
        return path.request().get();
    }

    public Response addWeather(String iata, String pointType, DataPoint dataPoint) {
        WebTarget path = collect.path("/weather/" + iata + "/" + pointType);
        return path.request().post(Entity.entity(dataPoint, MediaType.APPLICATION_JSON));
    }

    public Response addAirport(String iata, double latString, double longString) {
        iata = StringUtils.isEmpty(iata) ? " " : iata;
        StringBuilder path = new StringBuilder("/airport/")
                .append(iata)
                .append("/").append(latString)
                .append("/").append(longString);
        Invocation.Builder request = collect.path(path.toString()).request();
        return request.post(null);
    }

    public Response deleteAirport(String iata) {
        WebTarget path = collect.path("/airport/" + iata);
        return path.request().delete();
    }

    public Response exit() {
        return collect.path("/exit").request().get();
    }
}
