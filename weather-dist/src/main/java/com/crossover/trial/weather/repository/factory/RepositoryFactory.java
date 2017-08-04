package com.crossover.trial.weather.repository.factory;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.repository.*;

import static com.crossover.trial.weather.repository.factory.RepositoryType.IN_MEMORY;

public class RepositoryFactory {
    public static AirportRepository getAirportRepository(RepositoryType type) throws WeatherException {

        if (IN_MEMORY.equals(type)) {
            return AirportInMemoryRepositoryImpl.getInstance();
        } else {
            throw new WeatherException("repository type is not supported");
        }

    }

    public static AtmosphereRepository getAtmosphereRepository(RepositoryType type) throws WeatherException {

        if (IN_MEMORY.equals(type)) {
            return AtmosphereInMemoryRepositoryImpl.getInstance();
        } else {
            throw new WeatherException("repository type is not supported");
        }

    }

    public static FrequencyRepository getFrequencyRepository(RepositoryType type) throws WeatherException {

        if (IN_MEMORY.equals(type)) {
            return FrequencyInMemoryRepository.getInstance();
        } else {
            throw new WeatherException("repository type is not supported");
        }

    }
}
