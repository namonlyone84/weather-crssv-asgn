package com.crossover.trial.weather.repository;

import com.crossover.trial.weather.entity.Airport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AirportInMemoryRepositoryImpl implements AirportRepository {

    private static AirportRepository instance;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * all known airports
     */
    private Set<Airport> AIRPORTS_CACHE = new HashSet<>();

    private AirportInMemoryRepositoryImpl() {
    }

    public static synchronized AirportRepository getInstance() {
        if (instance == null) {
            instance = new AirportInMemoryRepositoryImpl();
        }

        return instance;
    }

    @Override
    public Airport findAirport(String iataCode) {
        lock.readLock().lock();
        Airport result = AIRPORTS_CACHE.stream()
                .filter(airport -> airport.getIata().equals(iataCode))
                .findFirst().orElse(null);
        lock.readLock().unlock();

        return result;
    }

    @Override
    public boolean exist(String iataCode) {
        lock.readLock().lock();
        boolean result = AIRPORTS_CACHE.stream()
                .anyMatch(airport -> airport.getIata().equalsIgnoreCase(iataCode));
        lock.readLock().unlock();

        return result;
    }

    @Override
    public List<Airport> findAll() {
        lock.readLock().lock();
        ArrayList<Airport> copyData = new ArrayList<>(AIRPORTS_CACHE);
        lock.readLock().unlock();

        return copyData;
    }

    @Override
    public Set<String> getAllAirportCodes() {
        lock.readLock().lock();
        Set<String> result = AIRPORTS_CACHE.stream().map(Airport::getIata).collect(Collectors.toSet());
        lock.readLock().unlock();

        return result;
    }

    @Override
    public Airport addAirport(String iataCode, double latitude, double longitude) {
        Airport airport = new Airport();
        airport.setIata(iataCode);
        airport.setLatitude(latitude);
        airport.setLongitude(longitude);

        lock.writeLock().lock();
        AIRPORTS_CACHE.add(airport);
        lock.writeLock().unlock();

        return airport;
    }

    @Override
    public boolean delete(String iataCode) {
        boolean result;

        lock.writeLock().lock();
        result = AIRPORTS_CACHE.removeIf(airport -> airport.getIata().equalsIgnoreCase(iataCode));
        lock.writeLock().unlock();

        return result;
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        AIRPORTS_CACHE.clear();
        lock.writeLock().lock();
    }
}
