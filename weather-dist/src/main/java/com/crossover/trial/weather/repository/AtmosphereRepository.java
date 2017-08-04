package com.crossover.trial.weather.repository;

import com.crossover.trial.weather.entity.AtmosphericInformation;

import java.util.List;

public interface AtmosphereRepository {

    AtmosphericInformation find(String iataCode);

    List<AtmosphericInformation> findAll();

    void save(String iataCode, AtmosphericInformation information);

    void clear();

    void delete(String iataCode);
}
