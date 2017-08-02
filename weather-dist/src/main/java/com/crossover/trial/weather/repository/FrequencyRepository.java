package com.crossover.trial.weather.repository;

import java.util.Set;

public interface FrequencyRepository {
    void update(String iata, Double radius);

    void clear();

    long getTotalRequest();

    int getRequestFrequency(String iataCode, int defaultValue);

    Set<String> getAllRequestIataCodes();

    Set<Double> getAllRadiuses();

    int getRadiusFrequency(Double radius);
}
