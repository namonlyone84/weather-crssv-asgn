package com.crossover.trial.weather.services;

import com.crossover.trial.weather.exception.ErrorCode;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.entities.Airport;
import com.crossover.trial.weather.entities.AtmosphericInformation;
import com.crossover.trial.weather.repository.AirportRepository;
import com.crossover.trial.weather.repository.AtmosphereRepository;
import com.crossover.trial.weather.repository.factory.RepositoryFactory;

import java.util.List;
import java.util.Set;

import static com.crossover.trial.weather.configuration.AppConfig.CURRENT_REPOSITORY_TYPE;

public class AirportService {
    private AirportRepository airportRepository;
    private AtmosphereRepository atmosphereRepository;

    public static final double EARTH_RADIUS = 6372.8;

    public AirportService() throws WeatherException {
        airportRepository = RepositoryFactory.getAirportRepository(CURRENT_REPOSITORY_TYPE);
        atmosphereRepository = RepositoryFactory.getAtmosphereRepository(CURRENT_REPOSITORY_TYPE);
    }

    public Set<String> getAllAirportCodes() {
        return airportRepository.getAllAirportCodes();
    }

    public Airport getAirport(String iataCode) {
        Airport result = airportRepository.findAirport(iataCode);

        if (result == null) {
            throw new WeatherException(ErrorCode.WEA_1002, iataCode, "IATA code");
        }

        return result;
    }

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public boolean deleteAirport(String iataCode) {
        return airportRepository.delete(iataCode);
    }

    /**
     * Add a new known airport to our list.
     *
     * @param iataCode 3 letter code
     * @param latitude in degrees
     * @param longitude in degrees
     *
     * @return the added airport
     */
    public Airport addAirport(String iataCode, double latitude, double longitude) {
        AtmosphericInformation atmosphericInformation = new AtmosphericInformation();
        atmosphereRepository.save(iataCode, atmosphericInformation);
        return airportRepository.addAirport(iataCode, latitude, longitude);
    }

    /**
     * Haversine distance between two airports.
     *
     * @param fromAirport airport 1
     * @param toAirport airport 2
     * @return the distance in KM
     */
    public double calculateDistance(Airport fromAirport, Airport toAirport) {
        double deltaLat = Math.toRadians(toAirport.getLatitude() - fromAirport.getLatitude());
        double deltaLon = Math.toRadians(toAirport.getLongitude() - fromAirport.getLongitude());
        double a =  Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(fromAirport.getLatitude()) * Math.cos(toAirport.getLatitude());
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS * c;
    }
}
