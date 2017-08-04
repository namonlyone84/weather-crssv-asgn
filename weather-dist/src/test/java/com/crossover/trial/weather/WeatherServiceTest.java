package com.crossover.trial.weather;

import com.crossover.trial.weather.entity.AtmosphericInformation;
import com.crossover.trial.weather.entity.DataPoint;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.repository.AirportRepository;
import com.crossover.trial.weather.repository.AtmosphereRepository;
import com.crossover.trial.weather.repository.FrequencyRepository;
import com.crossover.trial.weather.repository.factory.RepositoryFactory;
import com.crossover.trial.weather.service.WeatherService;
import com.crossover.trial.weather.util.ExceptionMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.crossover.trial.weather.common.DataPointType.*;
import static com.crossover.trial.weather.configuration.AppConfig.CURRENT_REPOSITORY_TYPE;
import static com.crossover.trial.weather.exception.ErrorCode.WEA_1002;
import static com.crossover.trial.weather.service.WeatherService.DATA_SIZE_HEALTH;
import static com.crossover.trial.weather.service.WeatherService.IATA_REQUEST_HEALTH;

public class WeatherServiceTest {
    private static final String[] IATA_CODES = new String[]{"A0", "A1", "A2", "A3"};

    private WeatherService weatherService = new WeatherService();

    private AirportRepository airportRepository = RepositoryFactory.getAirportRepository(CURRENT_REPOSITORY_TYPE);

    private AtmosphereRepository atmosphereRepository = RepositoryFactory.getAtmosphereRepository(CURRENT_REPOSITORY_TYPE);

    private FrequencyRepository frequencyRepository = RepositoryFactory.getFrequencyRepository(CURRENT_REPOSITORY_TYPE);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public WeatherServiceTest() throws IllegalAccessException, InstantiationException {
    }

    @Before
    public void setUp() {
        airportRepository.clear();
        atmosphereRepository.clear();
        frequencyRepository.clear();

        prepareAirport();
        updateWeather();
    }

    @Test
    public void testGetHealth_noWeatherRequest() {
        Map<String, Object> health = weatherService.getHealthStatus();
        int dataSize = (int) health.get(DATA_SIZE_HEALTH);
        Map<String, Double> iataRequests = (Map<String, Double>) health.get(IATA_REQUEST_HEALTH);

        Assert.assertEquals(IATA_CODES.length - 1, dataSize);
        Assert.assertEquals(IATA_CODES.length, iataRequests.size());

        // Request frequency of all airports should be zero
        Assert.assertTrue(iataRequests.values().stream().allMatch(frequency -> frequency == 0));
    }

    @Test
    public void testGetHealth_hasWeatherRequest() {
        // 20% request to A0
        frequencyRepository.update(IATA_CODES[0], 100d);
        // 80% request to A2
        for (int i = 0; i < 4; i++) {
            frequencyRepository.update(IATA_CODES[2], 100d);
        }

        Map<String, Object> health = weatherService.getHealthStatus();
        int dataSize = (int) health.get(DATA_SIZE_HEALTH);
        Map<String, Double> iataRequests = (Map<String, Double>) health.get(IATA_REQUEST_HEALTH);

        Assert.assertEquals(IATA_CODES.length - 1, dataSize);
        Assert.assertEquals(IATA_CODES.length, iataRequests.size());

        // Request frequency of all airports should be zero
        Assert.assertTrue(iataRequests.get(IATA_CODES[0]) == 0.2);
        Assert.assertTrue(iataRequests.get(IATA_CODES[1]) == 0);
        Assert.assertTrue(iataRequests.get(IATA_CODES[2]) == 0.8);
        Assert.assertTrue(iataRequests.get(IATA_CODES[3]) == 0);
    }

    @Test
    public void testGetWeather_noRadius() {
        List<AtmosphericInformation> weather = weatherService.getAirportWeather(IATA_CODES[0], 0);
        DataPoint windOfAirportA0 = weather.get(0).getWind();

        Assert.assertEquals(1, weather.size());
        Assert.assertTrue(isDataPointEqual(windOfAirportA0, 4, 6, 5.33, 10, 30));
    }

    @Test
    public void testGetWeather_insideRadius_oneAirportHasNoWeather() {
        double radius = 5594.5;
        List<AtmosphericInformation> weather = weatherService.getAirportWeather(IATA_CODES[1], radius);

        // should contain atmosphere of A0 and A1
        Assert.assertEquals(2, weather.size());
        // wind of A0
        Assert.assertTrue(weather.stream().anyMatch(information -> {
            return isDataPointEqual(information.getWind(), 4, 6, 5.33, 10, 30);
        }));
        // temperature of A1
        Assert.assertTrue(weather.stream().anyMatch(information -> {
            return isDataPointEqual(information.getTemperature(), 3, 5, 4.33, 9, 25);
        }));
    }

    @Test
    public void testGetWeather_upperBoundRadius_allAirportsHaveWeather() {
        // Add Humidity for A3
        DataPoint dataPoint = newDataPoint(6, 10, 8.00001, 15, 30);
        weatherService.addDataPoint(IATA_CODES[3], HUMIDITY.toString(), dataPoint);

        double radius = 5594.5;
        List<AtmosphericInformation> weather = weatherService.getAirportWeather(IATA_CODES[1], radius);

        // should contain atmosphere of A0, A1 and A3
        Assert.assertEquals(3, weather.size());

        // HUMIDITY of A3
        Assert.assertTrue(weather.stream().anyMatch(information -> {
            return isDataPointEqual(information.getHumidity(), 6, 10, 8.00001, 15, 30);
        }));
    }

    @Test
    public void testGetWeather_noAirport_throwException() {
        exception.expect(WeatherException.class);
        exception.expect(ExceptionMatcher.hasCode(WEA_1002));
        weatherService.getAirportWeather("NoAirport", 0);
    }

    private void prepareAirport() {
        /**
         * Real distance from A1 to:
         *      A0: 322km
         *      A2: 5595km
         *      A3: 5594.3km
         */
        // "General Edward Lawrence Logan Intl" airport
        airportRepository.addAirport(IATA_CODES[0], 42.364347, -71.005181);
        // "Newark Liberty Intl" airport
        airportRepository.addAirport(IATA_CODES[1], 40.6925, -74.168667);
        // "City" "London" airport
        airportRepository.addAirport(IATA_CODES[2], 51.505278, 0.055278);
        //"Stansted" "London" airport
        airportRepository.addAirport(IATA_CODES[3], 51.885, 0.235);
    }

    private void updateWeather() {
        DataPoint dataPoint = newDataPoint(4, 6, 5.33, 10, 30);
        weatherService.addDataPoint(IATA_CODES[0], WIND.toString(), dataPoint);

        dataPoint = newDataPoint(3, 5, 4.33, 9, 25);
        weatherService.addDataPoint(IATA_CODES[1], TEMPERATURE.toString(), dataPoint);

        dataPoint = newDataPoint(600, 700, 651.38, 1000, 20);
        weatherService.addDataPoint(IATA_CODES[2], PRESSURE.toString(), dataPoint);
    }

    private DataPoint newDataPoint(int first, int median, double mean, int last, int count) {
        DataPoint dataPoint = new DataPoint.Builder()
                .withFirst(first)
                .withMedian(median)
                .withMean(mean)
                .withLast(last)
                .withCount(count)
                .build();
        return dataPoint;
    }

    private boolean isDataPointEqual(DataPoint dataPoint, int first, int median, double mean, int last, int count) {
        return dataPoint != null
                && dataPoint.getFirst() == first
                && dataPoint.getSecond() == median
                && new BigDecimal(dataPoint.getMean()).equals(new BigDecimal(mean))
                && dataPoint.getThird() == last
                && dataPoint.getCount() == count;

    }
}
