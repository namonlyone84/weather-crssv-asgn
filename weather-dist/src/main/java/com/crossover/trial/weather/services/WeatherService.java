package com.crossover.trial.weather.services;

import com.crossover.trial.weather.common.DataPointType;
import com.crossover.trial.weather.exception.ErrorCode;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.common.WeatherHelper;
import com.crossover.trial.weather.entities.Airport;
import com.crossover.trial.weather.entities.AtmosphericInformation;
import com.crossover.trial.weather.entities.DataPoint;
import com.crossover.trial.weather.repository.AtmosphereRepository;
import com.crossover.trial.weather.repository.factory.RepositoryFactory;
import com.crossover.trial.weather.services.factory.ServiceRegistryFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.crossover.trial.weather.configuration.AppConfig.CURRENT_REPOSITORY_TYPE;

public class WeatherService {

    private AtmosphereRepository atmosphereRepository;

    private AirportService airportService;

    private StatisticService statisticService;

    public WeatherService() throws WeatherException, InstantiationException, IllegalAccessException {
        ServiceRegistryFactory serviceFactory = ServiceRegistryFactory.getInstance();
        airportService = serviceFactory.getService(AirportService.class);
        statisticService = serviceFactory.getService(StatisticService.class);

        atmosphereRepository = RepositoryFactory.getAtmosphereRepository(CURRENT_REPOSITORY_TYPE);
    }

    /**
     * Update the airports checkWeatherForAirport data with the collected data.
     *
     * @param iataCode  the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dataPoint a datapoint object holding pointType data
     * @throws WeatherException if the update can not be completed
     */
    public void addDataPoint(String iataCode, String pointType, DataPoint dataPoint) throws WeatherException {
        AtmosphericInformation atmosphericInformation = atmosphereRepository.find(iataCode);

        if (atmosphericInformation == null) {
            atmosphericInformation = new AtmosphericInformation();
        }

        updateWeatherInformation(atmosphericInformation, pointType, dataPoint);
        atmosphereRepository.save(iataCode, atmosphericInformation);
    }

    /**
     * update atmospheric information with the given data point for the given point type
     *
     * @param atmosphericInformation the atmospheric information object to update
     * @param pointType              the data point type as a string
     * @param dataPoint              the actual data point
     */
    private void updateWeatherInformation(AtmosphericInformation atmosphericInformation, String pointType,
                                          DataPoint dataPoint) throws WeatherException {
        final DataPointType weatherType = DataPointType.valueOf(pointType.toUpperCase());

        switch (weatherType) {
            case WIND:
                WeatherHelper.checkWind(dataPoint);
                atmosphericInformation.setWind(dataPoint);
                atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                return;
            case TEMPERATURE:
                WeatherHelper.checkTemperature(dataPoint);
                atmosphericInformation.setTemperature(dataPoint);
                atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                return;
            case HUMIDITY:
                WeatherHelper.checkHumidity(dataPoint);
                atmosphericInformation.setHumidity(dataPoint);
                atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                return;
            case PRESSURE:
                WeatherHelper.checkPressure(dataPoint);
                atmosphericInformation.setPressure(dataPoint);
                atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                return;
            case CLOUD_COVER:
                WeatherHelper.checkCloudCover(dataPoint);
                atmosphericInformation.setCloudCover(dataPoint);
                atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                return;
            case PRECIPITATION:
                WeatherHelper.checkPrecipitation(dataPoint);
                atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                return;

        }

        throw new WeatherException(ErrorCode.WEA_1001, "Illegal weather data type");
    }

    public Map<String, Object> getHealthStatus() {
        Map<String, Object> healthStatuses = new HashMap<>();

        healthStatuses.put("datasize", countDataSizeIn24Hours());
        healthStatuses.put("iata_freq", statisticService.computeRequestFrequencies());
        healthStatuses.put("radius_freq", statisticService.computeRadiusFrequencies());

        return healthStatuses;
    }

    private int countDataSizeIn24Hours() {
        return (int) atmosphereRepository.findAll().stream()
                .filter(information ->
                        WeatherHelper.isNotEmpty(information) && WeatherHelper.isUpdatedWithin24Hours(information))
                .count();
    }

    public List<AtmosphericInformation> getAirportWeather(String iataCode, double radius) {
        List<AtmosphericInformation> weather;

        if (radius == 0) {
            weather = new ArrayList<>();
            weather.add(atmosphereRepository.find(iataCode));
        } else {
            Airport fromAirport = airportService.getAirport(iataCode);

            weather = airportService.getAllAirports().stream()
                    .filter(toAirport -> airportService.calculateDistance(fromAirport, toAirport) <= radius)
                    .map(airport -> atmosphereRepository.find(airport.getIata()))
                    .filter(atmosphericInformation -> WeatherHelper.isNotEmpty(atmosphericInformation))
                    .collect(Collectors.toList());
        }

        return weather;
    }
}
