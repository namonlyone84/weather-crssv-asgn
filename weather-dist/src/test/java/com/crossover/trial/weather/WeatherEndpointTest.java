package com.crossover.trial.weather;

import com.crossover.trial.weather.configuration.AppConfig;
import com.crossover.trial.weather.endpoint.WeatherCollectorEndpointImpl;
import com.crossover.trial.weather.endpoint.WeatherQueryEndpointImpl;
import com.crossover.trial.weather.entity.AtmosphericInformation;
import com.crossover.trial.weather.entity.DataPoint;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.repository.AirportRepository;
import com.crossover.trial.weather.repository.AtmosphereRepository;
import com.crossover.trial.weather.repository.FrequencyRepository;
import com.crossover.trial.weather.repository.factory.RepositoryFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.crossover.trial.weather.configuration.AppConfig.CURRENT_REPOSITORY_TYPE;
import static org.junit.Assert.assertEquals;

public class WeatherEndpointTest {

    private WeatherQueryEndpointImpl _query = new WeatherQueryEndpointImpl();

    private WeatherCollectorEndpointImpl _update = new WeatherCollectorEndpointImpl();

    private AtmosphereRepository atmosphereRepository = RepositoryFactory.getAtmosphereRepository(CURRENT_REPOSITORY_TYPE);

    private AirportRepository airportRepository = RepositoryFactory.getAirportRepository(CURRENT_REPOSITORY_TYPE);

    private FrequencyRepository frequencyRepository = RepositoryFactory.getFrequencyRepository(CURRENT_REPOSITORY_TYPE);

    private Gson _gson = new Gson();

    private DataPoint _dp;

    public WeatherEndpointTest() throws WeatherException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName(AppConfig.class.getName());
    }

    @Before
    public void setUp() throws Exception {
        atmosphereRepository.clear();
        airportRepository.clear();
        frequencyRepository.clear();
        initAirports();
        _dp = new DataPoint.Builder()
                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
        _update.updateWeather("BOS", "wind", _gson.toJson(_dp));
        _query.weather("BOS", "0").getEntity();
    }

    private void initAirports() {
        airportRepository.addAirport("BOS", 42.364347, -71.005181);
        airportRepository.addAirport("EWR", 40.6925, -74.168667);
        airportRepository.addAirport("JFK", 40.639751, -73.778925);
        airportRepository.addAirport("LGA", 40.777245, -73.872608);
        airportRepository.addAirport("MMU", 40.79935, -74.4148747);
    }

    @Test
    public void testPing() throws Exception {
        String ping = _query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());
        assertEquals(5, pingResult.getAsJsonObject().get("iata_freq").getAsJsonObject().entrySet().size());
    }

    @Test
    public void testGet() throws Exception {
        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), _dp);
    }

    @Test
    public void testGetNearby() throws Exception {
        // check datasize response
        _update.updateWeather("JFK", "wind", _gson.toJson(_dp));
        _dp.setMean(40);
        _update.updateWeather("EWR", "wind", _gson.toJson(_dp));
        _dp.setMean(30);
        _update.updateWeather("LGA", "wind", _gson.toJson(_dp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("JFK", "200").getEntity();
        assertEquals(3, ais.size());
    }

    @Test
    public void testUpdate() throws Exception {

        DataPoint windDp = new DataPoint.Builder()
                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
        _update.updateWeather("BOS", "wind", _gson.toJson(windDp));
        _query.weather("BOS", "0").getEntity();

        String ping = _query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());

        DataPoint cloudCoverDp = new DataPoint.Builder()
                .withCount(4).withFirst(10).withMedian(60).withLast(100).withMean(50).build();
        _update.updateWeather("BOS", "cloud_cover", _gson.toJson(cloudCoverDp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), windDp);
        assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
    }

}