package com.crossover.trial.weather.configuration;

import com.crossover.trial.weather.endpoint.WeatherCollectorEndpointImpl;
import com.crossover.trial.weather.endpoint.WeatherQueryEndpointImpl;
import com.crossover.trial.weather.exception.GlobalExceptionHandler;
import com.crossover.trial.weather.repository.factory.RepositoryType;
import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {
    public static final RepositoryType CURRENT_REPOSITORY_TYPE = RepositoryType.IN_MEMORY;

    public static volatile boolean SERVER_STOP = false;

    public AppConfig() {
        register(WeatherCollectorEndpointImpl.class);
        register(WeatherQueryEndpointImpl.class);
        register(GlobalExceptionHandler.class);
    }
}
