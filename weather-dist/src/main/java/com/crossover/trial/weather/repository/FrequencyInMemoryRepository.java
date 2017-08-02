package com.crossover.trial.weather.repository;

import java.util.*;

public class FrequencyInMemoryRepository implements FrequencyRepository {
    private static FrequencyRepository instance;
    /**
     * Internal performance counter to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics {@link #ping()}
     */
    private Map<String, Integer> requestFrequency = new HashMap<>();

    private Map<Double, Integer> radiusFrequency = new HashMap<>();

    private FrequencyInMemoryRepository() {
    }

    public static synchronized FrequencyRepository getInstance() {
        if (instance == null) {
            instance = new FrequencyInMemoryRepository();
        }

        return instance;
    }


    @Override
    public void update(String iata, Double radius) {
        requestFrequency.put(iata, requestFrequency.getOrDefault(iata, 0) + 1);
        radiusFrequency.put(radius, radiusFrequency.getOrDefault(radius, 0) + 1);
    }

    @Override
    public void clear() {
        requestFrequency.clear();
        radiusFrequency.clear();
    }

    @Override
    public long getTotalRequest() {
        return requestFrequency.values().stream()
                .mapToLong(Integer::intValue).sum();
    }

    @Override
    public int getRequestFrequency(String iataCode, int defaultValue) {
        return requestFrequency.getOrDefault(iataCode, defaultValue);
    }

    @Override
    public Set<String> getAllRequestIataCodes() {
        return new HashSet<>(requestFrequency.keySet());
    }

    @Override
    public Set<Double> getAllRadiuses() {
        return new HashSet<>(radiusFrequency.keySet());
    }

    @Override
    public int getRadiusFrequency(Double radius) {
        return radiusFrequency.get(radius);
    }
}
