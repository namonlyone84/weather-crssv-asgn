package com.crossover.trial.weather.configuration;

import com.crossover.trial.weather.exception.GlobalExceptionHandler;
import com.crossover.trial.weather.endpoints.WeatherCollectorEndpointImpl;
import com.crossover.trial.weather.endpoints.WeatherQueryEndpointImpl;
import com.crossover.trial.weather.repository.factory.RepositoryType;
import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {
    public static final RepositoryType CURRENT_REPOSITORY_TYPE = RepositoryType.IN_MEMORY;

    public AppConfig() {
        register(WeatherCollectorEndpointImpl.class);
        register(WeatherQueryEndpointImpl.class);
        register(GlobalExceptionHandler.class);
    }
}
