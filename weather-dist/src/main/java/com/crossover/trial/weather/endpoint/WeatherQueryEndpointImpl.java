package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.WeatherQueryEndpoint;
import com.crossover.trial.weather.common.JSONHelper;
import com.crossover.trial.weather.entity.AtmosphericInformation;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.service.StatisticService;
import com.crossover.trial.weather.service.WeatherService;
import com.crossover.trial.weather.service.factory.ServiceRegistryFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class WeatherQueryEndpointImpl implements WeatherQueryEndpoint {

    public final static Logger LOGGER = Logger.getLogger("WeatherQuery");

    private WeatherService collectorService;

    private StatisticService statisticService;

    public WeatherQueryEndpointImpl() throws WeatherException, InstantiationException, IllegalAccessException {
        ServiceRegistryFactory serviceFactory = ServiceRegistryFactory.getInstance();
        collectorService = serviceFactory.getService(WeatherService.class);
        statisticService = serviceFactory.getService(StatisticService.class);
    }

    /**
     * Retrieve health and status information for the the query api. Returns information about how the number
     * of datapoints currently held in memory, the frequency of requests for each IATA code and the frequency of
     * requests for each radius.
     *
     * @return a JSON formatted dict with health information.
     */
    @GET
    @Path("/ping")
    public String ping() {
        return JSONHelper.toJson(collectorService.getHealthStatus());
    }

    /**
     * Retrieve the most up to date atmospheric information from the given airport and other airports in the given
     * radius.
     *
     * @param iata         the three letter airport code
     * @param radiusString the radius, in km, from which to collect checkWeatherForAirport data
     * @return an HTTP Response and a list of {@link AtmosphericInformation} from the requested airport and
     * airports in the given radius
     */
    @GET
    @Path("/weather/{iata}/{radius}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response weather(@PathParam("iata") String iata, @PathParam("radius") String radiusString) throws WeatherException {
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);

        List<AtmosphericInformation> whetherInformation = collectorService.getAirportWeather(iata, radius);
        statisticService.updateFrequencies(iata, radius);

        return Response.status(Response.Status.OK).entity(whetherInformation).build();
    }
}
