package com.crossover.trial.weather.service.factory;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistryFactory {

    private static ServiceRegistryFactory instance;

    private final Map<Class, Object> SERVICE_REGISTRY;

    private ServiceRegistryFactory() {
        SERVICE_REGISTRY = new HashMap<>();
    }

    public static synchronized ServiceRegistryFactory getInstance() {
        if (instance == null) {
            instance = new ServiceRegistryFactory();
        }
        return instance;
    }

    public<T extends Object> T getService(Class serviceClazz) throws IllegalAccessException, InstantiationException {
        synchronized (SERVICE_REGISTRY) {
            Object service = SERVICE_REGISTRY.get(serviceClazz);

            if (service == null) {
                service = serviceClazz.newInstance();
                SERVICE_REGISTRY.put(serviceClazz, service);
            }
            return (T) service;
        }
    }
}
