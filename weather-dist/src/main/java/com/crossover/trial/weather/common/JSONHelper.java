package com.crossover.trial.weather.common;

import com.google.gson.Gson;

public class JSONHelper {
    /**
     * shared gson json to object factory
     */
    private final static Gson gson = new Gson();

    static public <T extends Object> T fromJson(String json, Class clazz) {
        return (T) gson.fromJson(json, clazz);
    }

    static public String toJson(Object object) {
        return gson.toJson(object);
    }
}
