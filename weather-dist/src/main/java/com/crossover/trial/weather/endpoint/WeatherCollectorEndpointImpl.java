package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.WeatherCollectorEndpoint;
import com.crossover.trial.weather.common.DataPointType;
import com.crossover.trial.weather.common.JSONHelper;
import com.crossover.trial.weather.entity.Airport;
import com.crossover.trial.weather.entity.DataPoint;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.WeatherService;
import com.crossover.trial.weather.service.factory.ServiceRegistryFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

import static com.crossover.trial.weather.configuration.AppConfig.SERVER_STOP;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class WeatherCollectorEndpointImpl implements WeatherCollectorEndpoint {
    private final WeatherService collectorService;

    private final AirportService airportService;

    public WeatherCollectorEndpointImpl() throws WeatherException, InstantiationException, IllegalAccessException {
        ServiceRegistryFactory serviceFactory = ServiceRegistryFactory.getInstance();
        collectorService = serviceFactory.getService(WeatherService.class);
        airportService = serviceFactory.getService(AirportService.class);
    }

    /**
     * A liveliness check for the collection endpoint.
     *
     * @return 1 if the endpoint is alive functioning, 0 otherwise
     */
    @GET
    @Path("/ping")
    public Response ping() {
        return Response
                .status(Response.Status.OK)
                .entity("1")
                .build();
    }

    /**
     * Update the airports atmospheric information for a particular pointType with
     * json formatted data point information.
     *
     * @param iataCode  the 3 letter airport code
     * @param pointType the point type, {@link DataPointType} for a complete list
     * @param dataPoint a json dict containing mean, first, second, third and count keys
     * @return HTTP Response code
     */
    @POST
    @Path("/weather/{iata}/{pointType}")
    public Response updateWeather(@PathParam("iata") String iataCode,
                                  @PathParam("pointType") String pointType,
                                  String dataPoint) throws WeatherException {
        collectorService.addDataPoint(iataCode, pointType, JSONHelper.fromJson(dataPoint, DataPoint.class));
        return Response.status(Response.Status.OK).entity("OK").build();
    }

    /**
     * Return a list of known airports as a json formatted list
     *
     * @return HTTP Response code and a json formatted list of IATA codes
     */
    @GET
    @Path("/airports")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAirports() {
        Set<String> airports = airportService.getAllAirportCodes();
        return Response.status(Response.Status.OK).entity(airports).build();
    }

    /**
     * Retrieve airport data, including latitude and longitude for a particular airport
     *
     * @param iata the 3 letter airport code
     * @return an HTTP Response with a json representation of {@link Airport}
     */
    @GET
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAirport(@PathParam("iata") String iata) throws WeatherException {
        Airport airport = airportService.getAirport(iata);
        return Response.status(Response.Status.OK).entity(airport).build();
    }

    /**
     * Add a new airport to the known airport list.
     *
     * @param iata       the 3 letter airport code of the new airport
     * @param latString  the airport's latitude in degrees as a string [-90, 90]
     * @param longString the airport's longitude in degrees as a string [-180, 180]
     * @return HTTP Response code for the add operation
     */
    @POST
    @Path("/airport/{iata}/{lat}/{long}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAirport(@PathParam("iata") String iata,
                               @PathParam("lat") String latString,
                               @PathParam("long") String longString) throws WeatherException {
        airportService.addAirport(iata, latString, longString);
        return Response.status(Response.Status.OK).entity("OK").build();
    }

    /**
     * Remove an airport from the known airport list
     *
     * @param iata the 3 letter airport code
     * @return HTTP Repsonse code for the delete operation
     */
    @DELETE
    @Path("/airport/{iata}")
    public Response deleteAirport(@PathParam("iata") String iata) {
        boolean success = airportService.deleteAirport(iata);
        return Response
                .status(Response.Status.OK)
                .entity(success)
                .build();
    }

    @GET
    @Path("/exit")
    public Response exit() {
        // set flag to stop server
        SERVER_STOP = true;
        return Response.status(Response.Status.OK).entity("Server in shutdown progress..").build();
    }
}
