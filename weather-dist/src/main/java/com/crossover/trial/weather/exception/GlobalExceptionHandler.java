package com.crossover.trial.weather.exception;


import com.crossover.trial.weather.common.JSONHelper;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static com.crossover.trial.weather.exception.ErrorCode.*;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<WeatherException> {

    private static final ImmutableMap<ErrorCode, String> MESSAGES = ImmutableMap.<ErrorCode, String>builder()
            .put(WEA_1001, "Could not update atmospheric data. %1$s")
            .put(WEA_1002, "The %1$s is not found. Please check your provided %2$s information again")
            .put(WEA_1003, "Incorrect format of %1$s information. %2$s")
            .put(WEA_1004, "Duplication of %1$s")
            .build();

    @Override
    public Response toResponse(WeatherException exception) {
        String message = String.format(MESSAGES.get(exception.getErrorCode()), exception.getParams());

        return Response.status(exception.getErrorCode().getHttpStatus())
                .entity(JSONHelper.toJson(message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
