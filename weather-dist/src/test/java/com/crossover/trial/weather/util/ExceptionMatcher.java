package com.crossover.trial.weather.util;

import com.crossover.trial.weather.exception.ErrorCode;
import com.crossover.trial.weather.exception.WeatherException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ExceptionMatcher extends BaseMatcher<WeatherException> {
    private ErrorCode foundErrorCode;
    private ErrorCode expectedErrorCode;

    public static ExceptionMatcher hasCode(ErrorCode errorCode) {
        return new ExceptionMatcher(errorCode);
    }

    private ExceptionMatcher(ErrorCode errorCode) {
        this.expectedErrorCode = errorCode;
    }

    @Override
    public boolean matches(Object exception) {
        foundErrorCode = ((WeatherException) exception).getErrorCode();
        return foundErrorCode.equals(expectedErrorCode);
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(foundErrorCode)
                .appendText(" was not found instead of ")
                .appendValue(expectedErrorCode);
    }
}
