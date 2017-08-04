package com.crossover.trial.weather.service;

import com.crossover.trial.weather.entity.Airport;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.repository.FrequencyRepository;
import com.crossover.trial.weather.repository.factory.RepositoryFactory;
import com.crossover.trial.weather.service.factory.ServiceRegistryFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.crossover.trial.weather.configuration.AppConfig.CURRENT_REPOSITORY_TYPE;

public class StatisticService {
    private AirportService airportService;

    private FrequencyRepository frequencyRepository;

    public StatisticService() throws WeatherException, InstantiationException, IllegalAccessException {
        ServiceRegistryFactory serviceFactory = ServiceRegistryFactory.getInstance();
        airportService = serviceFactory.getService(AirportService.class);

        frequencyRepository = RepositoryFactory.getFrequencyRepository(CURRENT_REPOSITORY_TYPE);
    }

    /**
     * Records information about how often requests are made
     *
     * @param iata   an iata code
     * @param radius query radius
     */
    public void updateFrequencies(String iata, Double radius) {
        frequencyRepository.update(iata, radius);
    }

    public Map<String, Double> computeRequestFrequencies() {
        Map<String, Double> frequencies = new HashMap<>();

        long totalRequest = frequencyRepository.getTotalRequest();
        for (Airport airport : airportService.getAllAirports()) {
            double frequency = 0;

            if (totalRequest != 0) {
                frequency = (double) frequencyRepository.getRequestFrequency(airport.getIata(), 0) / totalRequest;
            }
            frequencies.put(airport.getIata(), frequency);
        }

        return frequencies;
    }

    public int[] computeRadiusFrequencies() {
        Set<Double> allRecordedRadius = frequencyRepository.getAllRadiuses();
        int maxRadius = allRecordedRadius.stream()
                .max(Double::compare)
                .orElse(1000d).intValue() + 1;

        int[] hist = new int[maxRadius];
        for (Double radius : allRecordedRadius) {
            int index = radius.intValue() % 10;
            hist[index] += frequencyRepository.getRadiusFrequency(radius);
        }

        return hist;
    }
}
