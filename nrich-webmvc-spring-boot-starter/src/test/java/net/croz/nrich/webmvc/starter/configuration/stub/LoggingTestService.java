package net.croz.nrich.webmvc.starter.configuration.stub;

import net.croz.nrich.logging.api.service.LoggingService;

import java.util.Map;

public class LoggingTestService implements LoggingService {

    @Override
    public void logInternalException(Exception exception, Map<String, ?> exceptionAuxiliaryData) {

    }

    @Override
    public void logInternalExceptionAtCompactVerbosityLevel(Exception exception, Map<String, ?> exceptionAuxiliaryData) {

    }

    @Override
    public void logInternalExceptionAtFullVerbosityLevel(Exception exception, Map<String, ?> exceptionAuxiliaryData) {

    }

    @Override
    public void logExternalException(String exceptionClassName, String exceptionMessage, Map<String, ?> exceptionAuxiliaryData) {

    }
}
