package com.crossover.trial.weather.repository;

import com.crossover.trial.weather.entity.AtmosphericInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AtmosphereInMemoryRepositoryImpl implements AtmosphereRepository {
    private static AtmosphereInMemoryRepositoryImpl instance;

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * atmospheric information for each airport, key by airport IATA code:
     */
    private final Map<String, AtmosphericInformation> WEATHER_BY_AIRPORT = new HashMap<>();

    private AtmosphereInMemoryRepositoryImpl() {
    }

    public static synchronized AtmosphereInMemoryRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new AtmosphereInMemoryRepositoryImpl();
        }

        return instance;
    }

    @Override
    public AtmosphericInformation find(String iataCode) {
        lock.readLock().lock();
        AtmosphericInformation atmosphere = WEATHER_BY_AIRPORT.get(iataCode);
        lock.readLock().unlock();

        return atmosphere;
    }

    @Override
    public List<AtmosphericInformation> findAll() {
        lock.readLock().lock();
        List<AtmosphericInformation> result = new ArrayList<>(WEATHER_BY_AIRPORT.values());
        lock.readLock().unlock();
        return result;
    }

    @Override
    public void save(String iataCode, AtmosphericInformation information) {
        lock.writeLock().lock();
        WEATHER_BY_AIRPORT.put(iataCode, information);
        lock.writeLock().unlock();
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        WEATHER_BY_AIRPORT.clear();
        lock.writeLock().unlock();
    }

    @Override
    public void delete(String iataCode) {
        lock.writeLock().lock();
        WEATHER_BY_AIRPORT.remove(iataCode);
        lock.writeLock().unlock();
    }
}
