package com.crossover.trial.weather;

import com.crossover.trial.weather.common.WeatherHelper;
import com.crossover.trial.weather.entity.Airport;
import com.crossover.trial.weather.entity.AtmosphericInformation;
import com.crossover.trial.weather.exception.ErrorCode;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.repository.AirportRepository;
import com.crossover.trial.weather.repository.AtmosphereRepository;
import com.crossover.trial.weather.repository.factory.RepositoryFactory;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.util.ExceptionMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.crossover.trial.weather.configuration.AppConfig.CURRENT_REPOSITORY_TYPE;
import static com.crossover.trial.weather.exception.ErrorCode.WEA_1002;

public class AirportServiceTest {
    private static final String[] IATA_CODES = new String[]{"BOS", "EWR", "LCY", "STN"};

    private AirportService airportService = new AirportService();

    private AirportRepository airportRepository = RepositoryFactory.getAirportRepository(CURRENT_REPOSITORY_TYPE);

    private AtmosphereRepository atmosphereRepository = RepositoryFactory.getAtmosphereRepository(CURRENT_REPOSITORY_TYPE);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        airportRepository.clear();
        atmosphereRepository.clear();

        // "General Edward Lawrence Logan Intl" airport
        airportService.addAirport(IATA_CODES[0], "42.364347", "-71.005181");
        // "Newark Liberty Intl" airport
        airportService.addAirport(IATA_CODES[1], "40.6925", "-74.168667");
        // "City" "London" airport
        airportService.addAirport(IATA_CODES[2], "51.505278", "0.055278");
        //"Stansted" "London" airport
        airportService.addAirport(IATA_CODES[3], "51.885", "0.235");
    }

    @Test
    public void testCalculateAirportsDistance_negativeLon() {
        Airport fromAirport = airportRepository.findAirport(IATA_CODES[0]);
        Airport toAirport = airportRepository.findAirport(IATA_CODES[1]);
        int realDistance = 322;

        Double distance = airportService.calculateDistance(fromAirport, toAirport);
        Assert.assertEquals(realDistance, distance.intValue());
    }

    @Test
    public void testCalculateAirportsDistance_positiveLon() {
        Airport fromAirport = airportRepository.findAirport(IATA_CODES[2]);
        Airport toAirport = airportRepository.findAirport(IATA_CODES[3]);
        int realDistance = 44;

        Double distance = airportService.calculateDistance(fromAirport, toAirport);
        Assert.assertEquals(realDistance, distance.intValue());
    }

    @Test
    public void testCalculateAirportsDistance_negativeLon1AndPositiveLon2() {
        Airport fromAirport = airportRepository.findAirport(IATA_CODES[1]);
        Airport toAirport = airportRepository.findAirport(IATA_CODES[3]);
        int realDistance = 5594;

        Double distance = airportService.calculateDistance(fromAirport, toAirport);
        Assert.assertEquals(realDistance, distance.intValue());
    }

    @Test
    public void testAddAirport_success() {
        String latitude = "30";
        String longitude = "-50";
        String iataCode = "EFG";

        airportService.addAirport(iataCode, latitude, longitude);
        Airport newAirport = airportRepository.findAirport(iataCode);
        AtmosphericInformation atmosphericInformation = atmosphereRepository.find(iataCode);

        Assert.assertEquals(new BigDecimal(latitude), new BigDecimal(newAirport.getLatitude()));
        Assert.assertEquals(new BigDecimal(longitude), new BigDecimal(newAirport.getLongitude()));

        Assert.assertTrue(atmosphericInformation != null);
        Assert.assertFalse(WeatherHelper.isNotEmpty(atmosphericInformation)); // should be empty
    }

    @Test
    public void testAddAirport_wrongLatitudeFormat_throwException() {
        String latitude = "1234.a";
        String longitude = "30.456";
        String iataCode = "NewAirport";

        exception.expect(WeatherException.class);
        exception.expect(ExceptionMatcher.hasCode(ErrorCode.WEA_1003));

        airportService.addAirport(iataCode, latitude, longitude);
    }

    @Test
    public void testAddAirport_wrongLongitudeFormat_throwException() {
        String latitude = "20.123";
        String longitude = "b5678";
        String iataCode = "NewAirport";

        exception.expect(WeatherException.class);
        exception.expect(ExceptionMatcher.hasCode(ErrorCode.WEA_1003));

        airportService.addAirport(iataCode, latitude, longitude);
    }

    @Test
    public void testAddAirport_latitudeUnderLower_throwException() {
        String latitude = "-90.132456";
        String longitude = "100";
        String iataCode = "NewAirport";

        exception.expect(WeatherException.class);
        exception.expect(ExceptionMatcher.hasCode(ErrorCode.WEA_1003));

        airportService.addAirport(iataCode, latitude, longitude);
    }

    @Test
    public void testAddAirport_latitudeBeyondUpper_throwException() {
        String latitude = "90.132456";
        String longitude = "100";
        String iataCode = "NewAirport";

        exception.expect(WeatherException.class);
        exception.expect(ExceptionMatcher.hasCode(ErrorCode.WEA_1003));

        airportService.addAirport(iataCode, latitude, longitude);
    }

    @Test
    public void testAddAirport_longitudeUnderLower_throwException() {
        String latitude = "30";
        String longitude = "-180.0000000000001";
        String iataCode = "NewAirport";

        exception.expect(WeatherException.class);
        exception.expect(ExceptionMatcher.hasCode(ErrorCode.WEA_1003));

        airportService.addAirport(iataCode, latitude, longitude);
        airportService.addAirport(iataCode, latitude, longitude);
    }

    @Test
    public void testAddAirport_longitudeBeyondUpper_throwException() {
        String latitude = "30";
        String longitude = "180.0000000000001";
        String iataCode = "NewAirport";

        exception.expect(WeatherException.class);
        exception.expect(ExceptionMatcher.hasCode(ErrorCode.WEA_1003));

        airportService.addAirport(iataCode, latitude, longitude);
        airportService.addAirport(iataCode, latitude, longitude);
    }

    @Test
    public void testAddAirport_duplicateAirport_throwException() {
        String latitude = "30";
        String longitude = "50";
        String iataCode = IATA_CODES[0];

        exception.expect(WeatherException.class);
        exception.expect(ExceptionMatcher.hasCode(ErrorCode.WEA_1004));

        airportService.addAirport(iataCode, latitude, longitude);
    }

    @Test
    public void testAddAirport_emptyIataCode_throwException() {
        String latitude = "30";
        String longitude = "50";
        String iataCode = " ";

        exception.expect(WeatherException.class);
        exception.expect(ExceptionMatcher.hasCode(ErrorCode.WEA_1003));

        airportService.addAirport(iataCode, latitude, longitude);
    }

    @Test
    public void testAddAirport_iataCodeMustContain3Character_throwException() {
        String latitude = "20";
        String longitude = "30";
        String iataCode = "ABCD";

        exception.expect(WeatherException.class);
        exception.expect(ExceptionMatcher.hasCode(ErrorCode.WEA_1003));

        airportService.addAirport(iataCode, latitude, longitude);
    }

    @Test
    public void testDeleteAirport_success() {
        String existingCode = IATA_CODES[0];

        int currentSize = airportRepository.findAll().size();
        boolean isDeleted = airportService.deleteAirport(existingCode);

        int afterSize = airportRepository.findAll().size();
        Airport airport = airportRepository.findAirport(existingCode);
        AtmosphericInformation atmosphere = atmosphereRepository.find(existingCode);

        Assert.assertTrue(isDeleted);
        Assert.assertEquals(currentSize - 1, afterSize);
        Assert.assertTrue(airport == null);
        Assert.assertTrue(atmosphere == null);
    }

    @Test
    public void testDelete_notAvailableAirport_throwException() {
        String existingCode = "NotAvailable";

        exception.expect(WeatherException.class);
        exception.expect(ExceptionMatcher.hasCode(WEA_1002));

        airportService.deleteAirport(existingCode);
    }

    @Test
    public void testGet_allAirports() {
        List<Airport> airports = airportService.getAllAirports();
        List<String> availableIatas = Arrays.asList(IATA_CODES);

        Assert.assertEquals(IATA_CODES.length, airports.size());
        airports.stream()
                .map(airport -> airport.getIata())
                .forEach(iata -> Assert.assertTrue(availableIatas.contains(iata)));
    }
}
