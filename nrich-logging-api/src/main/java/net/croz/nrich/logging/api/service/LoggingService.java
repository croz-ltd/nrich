package net.croz.nrich.logging.api.service;

import java.util.Map;

public interface LoggingService {

    void logInternalException(Exception exception, Map<String, ?> exceptionAuxiliaryData);

    void logInternalExceptionAtCompactVerbosityLevel(Exception exception, Map<String, ?> exceptionAuxiliaryData);

    void logInternalExceptionAtFullVerbosityLevel(Exception exception, Map<String, ?> exceptionAuxiliaryData);

    void logExternalException(String exceptionClassName, String exceptionMessage, Map<String, ?> exceptionAuxiliaryData);
}
