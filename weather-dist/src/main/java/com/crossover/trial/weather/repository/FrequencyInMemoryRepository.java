package com.crossover.trial.weather.repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    private ReadWriteLock lock = new ReentrantReadWriteLock();

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
        lock.writeLock().lock();
        requestFrequency.put(iata, requestFrequency.getOrDefault(iata, 0) + 1);
        radiusFrequency.put(radius, radiusFrequency.getOrDefault(radius, 0) + 1);
        lock.writeLock().unlock();
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        requestFrequency.clear();
        radiusFrequency.clear();
        lock.writeLock().unlock();
    }

    @Override
    public long getTotalRequest() {
        lock.readLock().lock();
        long result = requestFrequency.values().stream()
                .mapToLong(Integer::intValue).sum();
        lock.readLock().unlock();
        return result;
    }

    @Override
    public int getRequestFrequency(String iataCode, int defaultValue) {
        lock.readLock().lock();
        int result = requestFrequency.getOrDefault(iataCode, defaultValue);
        lock.readLock().unlock();
        return result;
    }

    @Override
    public Set<Double> getAllRadiuses() {
        lock.readLock().lock();
        Set<Double> result = new HashSet<>(radiusFrequency.keySet());
        lock.readLock().unlock();
        return result;
    }

    @Override
    public int getRadiusFrequency(Double radius) {
        lock.readLock().lock();
        int result = radiusFrequency.get(radius);
        lock.readLock().unlock();
        return result;
    }
}
