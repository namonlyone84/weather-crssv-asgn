package com.crossover.trial.weather.repository;

import com.crossover.trial.weather.entities.Airport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AirportInMemoryRepositoryImpl implements AirportRepository {

    private static AirportRepository instance;

    /** all known airports */
    public List<Airport> AIRPORTS_CATCH = new ArrayList<>();


    private AirportInMemoryRepositoryImpl () {
    }

    public static synchronized AirportRepository getInstance() {
        if (instance == null) {
            instance = new AirportInMemoryRepositoryImpl();
        }

        return instance;
    }

    @Override
    public Airport findAirport(String iataCode) {
        return AIRPORTS_CATCH.stream()
                .filter(airport -> airport.getIata().equals(iataCode))
                .findFirst().orElse(null);
    }

    @Override
    public List<Airport> findAll() {
        ArrayList<Airport> copyData = new ArrayList<>();
        copyData.addAll(AIRPORTS_CATCH);
        return copyData;
    }

    @Override
    public Set<String> getAllAirportCodes() {
        return AIRPORTS_CATCH.stream().map(Airport::getIata).collect(Collectors.toSet());
    }

    @Override
    public Airport addAirport(String iataCode, double latitude, double longitude) {
        Airport airport = new Airport();
        AIRPORTS_CATCH.add(airport);

        airport.setIata(iataCode);
        airport.setLatitude(latitude);
        airport.setLatitude(longitude);
        return airport;
    }

    @Override
    public boolean delete(String iataCode) {
        return AIRPORTS_CATCH.removeIf(airport -> airport.getIata().equalsIgnoreCase(iataCode));
    }

    @Override
    public void clear() {
        AIRPORTS_CATCH.clear();
    }
}
