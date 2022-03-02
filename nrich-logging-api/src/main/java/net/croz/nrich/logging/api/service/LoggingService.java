package net.croz.nrich.logging.api.service;

import java.util.Map;

/**
 * Performs logging of exceptions at specified verbosity level and logging level (can be configured for each exception).
 */
public interface LoggingService {

    /**
     * Logs exception. Exception verbosity and logging level are resolved from configuration.
     * If no exception or verbosity level is defined defaults are used (LoggingLevel.ERROR and LoggingVerbosityLevel.FULL).
     *
     * @param exception              exception to log
     * @param exceptionAuxiliaryData additional data that should be logged
     */
    void logInternalException(Exception exception, Map<String, ?> exceptionAuxiliaryData);

    /**
     * Logs exception on compact verbosity level. Logging level is resolved from configuration for exception or LoggingLevel.ERROR is used.
     *
     * @param exception              exception to log
     * @param exceptionAuxiliaryData additional data that should be logged
     */
    void logInternalExceptionAtCompactVerbosityLevel(Exception exception, Map<String, ?> exceptionAuxiliaryData);

    /**
     * Logs exception on full verbosity level. Logging level is resolved from configuration for exception or LoggingLevel.ERROR is used.
     *
     * @param exception              exception to log
     * @param exceptionAuxiliaryData additional data that should be logged
     */
    void logInternalExceptionAtFullVerbosityLevel(Exception exception, Map<String, ?> exceptionAuxiliaryData);

    /**
     * Logs external exception (i.e. exceptions for which a class is not on classpath)
     *
     * @param exceptionClassName     class name of exception
     * @param exceptionMessage       exception message
     * @param exceptionAuxiliaryData additional data that should be logged
     */
    void logExternalException(String exceptionClassName, String exceptionMessage, Map<String, ?> exceptionAuxiliaryData);

}
