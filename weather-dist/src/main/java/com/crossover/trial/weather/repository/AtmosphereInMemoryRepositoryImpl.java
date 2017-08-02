package com.crossover.trial.weather.repository;

import com.crossover.trial.weather.entities.AtmosphericInformation;

import java.util.*;

public class AtmosphereInMemoryRepositoryImpl implements AtmosphereRepository {
    private static AtmosphereInMemoryRepositoryImpl instance;

    /** atmospheric information for each airport, key by airport IATA code:*/
    private final Map<String, AtmosphericInformation> WEATHER_BY_AIRPORT_IATA = new HashMap<>();


    private AtmosphereInMemoryRepositoryImpl () {
    }

    public static synchronized AtmosphereInMemoryRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new AtmosphereInMemoryRepositoryImpl();
        }

        return instance;
    }

    @Override
    public AtmosphericInformation find(String iataCode) {
        return WEATHER_BY_AIRPORT_IATA.get(iataCode);
    }

    @Override
    public List<AtmosphericInformation> findAll() {
        return new ArrayList<>(WEATHER_BY_AIRPORT_IATA.values());
    }

    @Override
    public void save(String iataCode, AtmosphericInformation information) {
        WEATHER_BY_AIRPORT_IATA.put(iataCode, information);
    }

    @Override
    public void clear() {
        WEATHER_BY_AIRPORT_IATA.clear();
    }
}
