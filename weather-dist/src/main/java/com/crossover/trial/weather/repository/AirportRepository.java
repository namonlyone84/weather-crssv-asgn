package com.crossover.trial.weather.repository;

import com.crossover.trial.weather.entities.Airport;

import java.util.List;
import java.util.Set;

public interface AirportRepository {
    /**
     * Given an iataCode find the airport data
     *
     * @param iataCode as a string
     * @return airport data or null if not found
     */
    Airport findAirport(String iataCode);

    List<Airport> findAll();

    /**
     * Get all available airport IATA codes
     * @return list of airport IATA codes
     */
    Set<String> getAllAirportCodes();

    /**
     * Add a new known airport to our list.
     *
     * @param iataCode 3 letter code
     * @param latitude in degrees
     * @param longitude in degrees
     *
     * @return the added airport
     */
    Airport addAirport(String iataCode, double latitude, double longitude);

    boolean delete(String iataCode);

    void clear();
}
