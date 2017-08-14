package com.crossover.trial.weather.exception;

/**
 * An internal exception marker
 * <p>
 * Why need to extends RuntimeException?
 * Because DoNotChangeTest not allow to modified the Endpoints API including adding checking
 * exception into the method signal. Extending RuntimeException would help to workaround this.
 * <p>
 * The ideal implementation should be extending the checked Exception.
 */
public class WeatherException extends RuntimeException {
    private ErrorCode errorCode;
    private Object[] params;

    public WeatherException(String message) {
        super(message);
    }

    public WeatherException(ErrorCode errorCode, Object... params) {
        this.errorCode = errorCode;
        this.params = params;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public Object[] getParams() {
        return params;
    }
}
