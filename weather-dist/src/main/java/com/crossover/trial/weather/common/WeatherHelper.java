package com.crossover.trial.weather.common;

import com.crossover.trial.weather.entity.AtmosphericInformation;
import com.crossover.trial.weather.entity.DataPoint;
import com.crossover.trial.weather.exception.ErrorCode;
import com.crossover.trial.weather.exception.WeatherException;

public class WeatherHelper {
    private static final int TEMPERATURE_MIN = -50;
    private static final int TEMPERATURE_MAX = 100;

    private static final int HUMIDITY_MIN = 0;
    private static final int HUMIDITY_MAX = 100;

    private static final int PRESSURE_MIN = 650;
    private static final int PRESSURE_MAX = 800;

    private static final int CLOUD_MIN = 0;
    private static final int CLOUD_MAX = 100;

    private static final int PRECIPITATION_MIN = 0;
    private static final int PRECIPITATION_MAX = 100;

    private static final long MILLI_SECOND_PER_DAY = 24 * 60 * 60 * 1000;

    public static void checkWind(DataPoint wind) throws WeatherException {
        if (wind.getMean() < 0) {
            throwDataPointError();
        }
    }

    public static void checkTemperature(DataPoint temperature) throws WeatherException {
        if (!(temperature.getMean() >= TEMPERATURE_MIN && temperature.getMean() < TEMPERATURE_MAX)) {
            throwDataPointError();
        }
    }

    public static void checkHumidity(DataPoint humidity) throws WeatherException {
        if (!(humidity.getMean() >= HUMIDITY_MIN && humidity.getMean() < HUMIDITY_MAX)) {
            throwDataPointError();
        }
    }

    public static void checkPressure(DataPoint pressure) throws WeatherException {
        if (!(pressure.getMean() >= PRESSURE_MIN && pressure.getMean() < PRESSURE_MAX)) {
            throwDataPointError();
        }
    }

    public static void checkCloudCover(DataPoint cloudCover) throws WeatherException {
        if (!(cloudCover.getMean() >= CLOUD_MIN && cloudCover.getMean() < CLOUD_MAX)) {
            throwDataPointError();
        }
    }

    public static void checkPrecipitation(DataPoint precipitation) throws WeatherException {
        if (!(precipitation.getMean() >=PRECIPITATION_MIN && precipitation.getMean() < PRECIPITATION_MAX)) {
            throwDataPointError();
        }
    }

    private static void throwDataPointError () throws WeatherException {
        throw new WeatherException(ErrorCode.WEA_1001, "Illegal data point value");
    }

    public static boolean isNotEmpty(AtmosphericInformation information) {
        return (information != null) &&
                (information.getCloudCover() != null
                        || information.getHumidity() != null
                        || information.getPressure() != null
                        || information.getPrecipitation() != null
                        || information.getTemperature() != null
                        || information.getWind() != null);
    }

    public static boolean isUpdatedWithin24Hours(AtmosphericInformation information) {
        return information.getLastUpdateTime() > System.currentTimeMillis() - MILLI_SECOND_PER_DAY;
    }
}
