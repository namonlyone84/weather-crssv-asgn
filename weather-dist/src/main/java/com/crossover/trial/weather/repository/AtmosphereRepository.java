package com.crossover.trial.weather.repository;

import com.crossover.trial.weather.entities.AtmosphericInformation;

import java.util.List;

public interface AtmosphereRepository {

    AtmosphericInformation find(String iataCode);

    List<AtmosphericInformation> findAll();

    void save(String iataCode, AtmosphericInformation information);

    void clear();
}
