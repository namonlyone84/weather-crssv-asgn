package com.crossover.trial.weather.exception;

import org.eclipse.jetty.http.HttpStatus;

public enum ErrorCode {
    WEA_1001(HttpStatus.BAD_REQUEST_400),
    WEA_1002(HttpStatus.NOT_FOUND_404);

    private int httpStatus;

    private ErrorCode() {
    }

    private ErrorCode(int httpStatus) {
        httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
