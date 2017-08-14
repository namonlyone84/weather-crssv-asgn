package com.crossover.trial.weather.common;

import com.crossover.trial.weather.exception.ErrorCode;
import com.crossover.trial.weather.exception.WeatherException;
import org.apache.commons.lang3.StringUtils;

public class AirportHelper {

    public static void checkLatLonRange(double lat, double lon) {
        if ((lat < -90 || lat > 90) || (lon < -180 || lon > 180)) {
            throw new WeatherException(ErrorCode.WEA_1003,
                    "latitude/longitude", "Should be Latitude [-90, 90], longitude [-180, 180].");
        }
    }

    public static void checkIATAFormat(String iataCode) {
        if (StringUtils.isBlank(iataCode) || iataCode.length() != 3) {
            throw new WeatherException(ErrorCode.WEA_1003,
                    "IATA Code", "IATA Code should contains 3 character.");
        }
    }
}
