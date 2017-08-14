package com.crossover.trial.weather;

import com.crossover.trial.weather.entity.AtmosphericInformation;
import com.crossover.trial.weather.entity.DataPoint;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.repository.AirportRepository;
import com.crossover.trial.weather.repository.AtmosphereRepository;
import com.crossover.trial.weather.repository.factory.RepositoryFactory;
import com.crossover.trial.weather.service.WeatherService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.crossover.trial.weather.common.DataPointType.*;
import static com.crossover.trial.weather.configuration.AppConfig.CURRENT_REPOSITORY_TYPE;
import static com.crossover.trial.weather.exception.ErrorCode.WEA_1001;
import static com.crossover.trial.weather.util.ExceptionMatcher.hasCode;

public class WeatherServiceDataPointTest {
    private static final String IATA = "ONE";

    private WeatherService weatherService = new WeatherService();

    private AirportRepository airportRepository = RepositoryFactory.getAirportRepository(CURRENT_REPOSITORY_TYPE);

    private AtmosphereRepository atmosphereRepository = RepositoryFactory.getAtmosphereRepository(CURRENT_REPOSITORY_TYPE);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public WeatherServiceDataPointTest() throws IllegalAccessException, InstantiationException {
    }

    @Before
    public void setUp() {
        airportRepository.clear();
        atmosphereRepository.clear();

        airportRepository.addAirport(IATA, 123, 345);
    }

    @Test
    public void testAddDataPoint_wind_success() {
        long beforeUpdate = System.currentTimeMillis();
        DataPoint dataPoint = newDataPoint(3, 7, 5.33, 10, 20);

        weatherService.addDataPoint(IATA, WIND.toString(), dataPoint);
        AtmosphericInformation information = atmosphereRepository.find(IATA);

        Assert.assertEquals(information.getWind(), dataPoint);
        Assert.assertTrue(information.getLastUpdateTime() >= beforeUpdate);
    }

    @Test
    public void testAddDataPoint_temperature_success() {
        long beforeUpdate = System.currentTimeMillis();
        DataPoint dataPoint = newDataPoint(3, 7, 5.33, 10, 20);

        weatherService.addDataPoint(IATA, TEMPERATURE.toString(), dataPoint);
        AtmosphericInformation information = atmosphereRepository.find(IATA);

        Assert.assertEquals(information.getTemperature(), dataPoint);
        Assert.assertTrue(information.getLastUpdateTime() >= beforeUpdate);
    }

    @Test
    public void testAddDataPoint_humidity_success() {
        long beforeUpdate = System.currentTimeMillis();
        DataPoint dataPoint = newDataPoint(3, 7, 5.33, 10, 20);

        weatherService.addDataPoint(IATA, HUMIDITY.toString(), dataPoint);
        AtmosphericInformation information = atmosphereRepository.find(IATA);

        Assert.assertEquals(information.getHumidity(), dataPoint);
        Assert.assertTrue(information.getLastUpdateTime() >= beforeUpdate);
    }

    @Test
    public void testAddDataPoint_pressure_success() {
        long beforeUpdate = System.currentTimeMillis();
        DataPoint dataPoint = newDataPoint(600, 700, 651.38, 1000, 20);

        weatherService.addDataPoint(IATA, PRESSURE.toString(), dataPoint);
        AtmosphericInformation information = atmosphereRepository.find(IATA);

        Assert.assertEquals(information.getPressure(), dataPoint);
        Assert.assertTrue(information.getLastUpdateTime() >= beforeUpdate);
    }

    @Test
    public void testAddDataPoint_cloudCover_success() {
        long beforeUpdate = System.currentTimeMillis();
        DataPoint dataPoint = newDataPoint(3, 7, 5.33, 10, 20);

        weatherService.addDataPoint(IATA, CLOUD_COVER.toString(), dataPoint);
        AtmosphericInformation information = atmosphereRepository.find(IATA);

        Assert.assertEquals(information.getCloudCover(), dataPoint);
        Assert.assertTrue(information.getLastUpdateTime() >= beforeUpdate);
    }

    @Test
    public void testAddDataPoint_precipitation_success() {
        long beforeUpdate = System.currentTimeMillis();
        DataPoint dataPoint = newDataPoint(3, 7, 5.33, 10, 20);

        weatherService.addDataPoint(IATA, PRECIPITATION.toString(), dataPoint);
        AtmosphericInformation information = atmosphereRepository.find(IATA);

        Assert.assertEquals(information.getPrecipitation(), dataPoint);
        Assert.assertTrue(information.getLastUpdateTime() >= beforeUpdate);
    }

    @Test
    public void testAddDataPoint_temperatureLowerBound_throwException() {
        DataPoint dataPoint = newDataPoint(3, 7, -50.00000000001, 10, 20);

        exception.expect(WeatherException.class);
        exception.expect(hasCode(WEA_1001));

        weatherService.addDataPoint(IATA, TEMPERATURE.toString(), dataPoint);
    }

    @Test
    public void testAddDataPoint_temperatureUpperBound_throwException() {
        DataPoint dataPoint = newDataPoint(3, 7, 100, 10, 20);

        exception.expect(WeatherException.class);
        exception.expect(hasCode(WEA_1001));

        weatherService.addDataPoint(IATA, TEMPERATURE.toString(), dataPoint);
    }

    @Test
    public void testAddDataPoint_incorrectType_throwException() {
        DataPoint dataPoint = newDataPoint(3, 7, 30, 10, 20);

        exception.expect(WeatherException.class);
        exception.expect(hasCode(WEA_1001));

        weatherService.addDataPoint(IATA, "NOTYPE", dataPoint);
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
}
