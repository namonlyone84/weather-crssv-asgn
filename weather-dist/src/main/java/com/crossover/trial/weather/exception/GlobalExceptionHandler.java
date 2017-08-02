package com.crossover.trial.weather.exception;


import jersey.repackaged.com.google.common.collect.ImmutableMap;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static com.crossover.trial.weather.exception.ErrorCode.WEA_1001;
import static com.crossover.trial.weather.exception.ErrorCode.WEA_1002;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<WeatherException> {
    private ImmutableMap<ErrorCode, String> MESSAGES = ImmutableMap.<ErrorCode, String>builder()
            .put(WEA_1001, "Could not update atmospheric data. %1$s")
            .put(WEA_1002, "The %1$s is not found. Please check again your provided %2$s information")
            .build();

    @Override
    public Response toResponse(WeatherException exception) {
        String message = String.format(MESSAGES.get(exception.getErrorCode()), exception.getParams());

        return Response.status(exception.getErrorCode().getHttpStatus())
                .entity(message)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
