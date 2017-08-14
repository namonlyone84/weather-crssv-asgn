package com.crossover.trial.weather.service;

import com.crossover.trial.weather.common.DataPointType;
import com.crossover.trial.weather.common.WeatherHelper;
import com.crossover.trial.weather.entity.Airport;
import com.crossover.trial.weather.entity.AtmosphericInformation;
import com.crossover.trial.weather.entity.DataPoint;
import com.crossover.trial.weather.exception.ErrorCode;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.repository.AtmosphereRepository;
import com.crossover.trial.weather.repository.factory.RepositoryFactory;
import com.crossover.trial.weather.service.factory.ServiceRegistryFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.crossover.trial.weather.configuration.AppConfig.CURRENT_REPOSITORY_TYPE;

public class WeatherService {
    public static final String DATA_SIZE_HEALTH = "datasize";
    public static final String IATA_REQUEST_HEALTH = "iata_freq";
    public static final String RADIUS_REQUEST_HEALTH = "radius_freq";

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
        airportService.checkNotFoundAirport(iataCode);
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

        try {
            final DataPointType weatherType = DataPointType.valueOf(pointType.toUpperCase());

            switch (weatherType) {
                case WIND:
                    WeatherHelper.checkWind(dataPoint);
                    atmosphericInformation.setWind(dataPoint);
                    atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                    break;
                case TEMPERATURE:
                    WeatherHelper.checkTemperature(dataPoint);
                    atmosphericInformation.setTemperature(dataPoint);
                    atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                    break;
                case HUMIDITY:
                    WeatherHelper.checkHumidity(dataPoint);
                    atmosphericInformation.setHumidity(dataPoint);
                    atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                    break;
                case PRESSURE:
                    WeatherHelper.checkPressure(dataPoint);
                    atmosphericInformation.setPressure(dataPoint);
                    atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                    break;
                case CLOUD_COVER:
                    WeatherHelper.checkCloudCover(dataPoint);
                    atmosphericInformation.setCloudCover(dataPoint);
                    atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                    break;
                case PRECIPITATION:
                    WeatherHelper.checkPrecipitation(dataPoint);
                    atmosphericInformation.setPrecipitation(dataPoint);
                    atmosphericInformation.setLastUpdateTime(System.currentTimeMillis());
                    break;
            }
        } catch (IllegalArgumentException exception) {
            throw new WeatherException(ErrorCode.WEA_1001, "Illegal weather data type");
        }

    }

    public Map<String, Object> getHealthStatus() {
        Map<String, Object> healthStatuses = new HashMap<>();

        healthStatuses.put(DATA_SIZE_HEALTH, countDataSizeIn24Hours());
        healthStatuses.put(IATA_REQUEST_HEALTH, statisticService.computeRequestFrequencies());
        healthStatuses.put(RADIUS_REQUEST_HEALTH, statisticService.computeRadiusFrequencies());

        return healthStatuses;
    }

    public List<AtmosphericInformation> getAirportWeather(String iataCode, double radius) throws WeatherException {
        airportService.checkNotFoundAirport(iataCode);

        List<AtmosphericInformation> weather;
        if (radius == 0) {
            weather = getWeatherForSingleAirport(iataCode);
        } else {
            weather = getWeatherWithinRadius(iataCode, radius);
        }
        return weather;
    }

    private int countDataSizeIn24Hours() {
        return (int) atmosphereRepository.findAll().stream()
                .filter(information ->
                        WeatherHelper.isNotEmpty(information) && WeatherHelper.isUpdatedWithin24Hours(information))
                .count();
    }

    private List<AtmosphericInformation> getWeatherForSingleAirport(String iataCode) {
        List<AtmosphericInformation> weather = new ArrayList<>();
        AtmosphericInformation atmosphere = atmosphereRepository.find(iataCode);
        if (WeatherHelper.isNotEmpty(atmosphere)) {
            weather.add(atmosphere);
        }
        return weather;
    }

    private List<AtmosphericInformation> getWeatherWithinRadius(String iataCode, double radius) {
        Airport fromAirport = airportService.getAirport(iataCode);

        List<AtmosphericInformation> weather = airportService.getAllAirports().stream()
                .filter(toAirport -> airportService.calculateDistance(fromAirport, toAirport) <= radius)
                .map(airport -> atmosphereRepository.find(airport.getIata()))
                .filter(atmosphericInformation -> WeatherHelper.isNotEmpty(atmosphericInformation))
                .collect(Collectors.toList());
        return weather;
    }
}
