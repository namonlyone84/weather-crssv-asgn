package com.crossover.trial.weather.exception;

import org.eclipse.jetty.http.HttpStatus;

public enum ErrorCode {
    // Illegal atmosphere data
    WEA_1001(HttpStatus.BAD_REQUEST_400),

    // Data not found
    WEA_1002(HttpStatus.NOT_FOUND_404),

    // Wrong format data
    WEA_1003(HttpStatus.BAD_REQUEST_400),

    // Duplicate data
    WEA_1004(HttpStatus.CONFLICT_409);

    private int httpStatus;

    private ErrorCode() {
    }

    private ErrorCode(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }
}
