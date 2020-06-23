package net.croz.nrich.webmvc.starter.configuration.stub;

import net.croz.nrich.logging.api.service.LoggingService;

import java.util.Map;

public class LoggingTestService implements LoggingService {

    @Override
    public void logInternalException(final Exception exception, final Map<String, ?> exceptionAuxiliaryData) {

    }

    @Override
    public void logInternalExceptionAtCompactVerbosityLevel(final Exception exception, final Map<String, ?> exceptionAuxiliaryData) {

    }

    @Override
    public void logInternalExceptionAtFullVerbosityLevel(final Exception exception, final Map<String, ?> exceptionAuxiliaryData) {

    }

    @Override
    public void logExternalException(final String exceptionClassName, final String exceptionMessage, final Map<String, ?> exceptionAuxiliaryData) {

    }
}
