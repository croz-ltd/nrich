package net.croz.nrich.logging.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.logging.api.model.LoggingLevel;
import net.croz.nrich.logging.api.model.LoggingVerbosityLevel;
import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.logging.constant.LoggingConstants;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class Slf4jLoggingService implements LoggingService {

    private final MessageSource messageSource;

    @Override
    public void logInternalException(final Exception exception, final Map<String, ?> exceptionAuxiliaryData) {
        final LoggingVerbosityLevel configuredLoggingVerbosityLevelForException = fetchConfiguredLoggingVerbosityLevelForException(exception);

        if (configuredLoggingVerbosityLevelForException == LoggingVerbosityLevel.COMPACT) {
            logInternalExceptionAtCompactVerbosityLevel(exception, exceptionAuxiliaryData);
        }
        else if (configuredLoggingVerbosityLevelForException == LoggingVerbosityLevel.FULL) {
            logInternalExceptionAtFullVerbosityLevel(exception, exceptionAuxiliaryData);
        }
    }

    @Override
    public void logInternalExceptionAtCompactVerbosityLevel(final Exception exception, final Map<String, ?> exceptionAuxiliaryData) {
        final String exceptionAuxiliaryDataMessage = prepareExceptionAuxiliaryDataMessage(exceptionAuxiliaryData, ", ");
        final String logMessage = String.format(LoggingConstants.EXCEPTION_COMPACT_LEVEL_LOG_FORMAT, fetchClassNameForException(exception), fetchMessageForException(exception), exceptionAuxiliaryDataMessage);
        final LoggingLevel loggingLevel = fetchConfiguredLoggingLevelForException(fetchClassNameForException(exception));

        logOnLevel(loggingLevel, logMessage);
    }

    @Override
    public void logInternalExceptionAtFullVerbosityLevel(final Exception exception, final Map<String, ?> exceptionAuxiliaryData) {
        final String exceptionInfoString = String.format(LoggingConstants.EXCEPTION_FULL_LEVEL_MESSAGE_FORMAT, fetchClassNameForException(exception), fetchMessageForException(exception));
        final LoggingLevel loggingLevel = fetchConfiguredLoggingLevelForException(fetchClassNameForException(exception));

        logOnLevel(loggingLevel,  "Exception occurred", exception);

        final String exceptionAuxiliaryDataMessage = prepareExceptionAuxiliaryDataMessage(exceptionAuxiliaryData, System.lineSeparator());
        final String exceptionLogMessage = String.format(LoggingConstants.EXCEPTION_FULL_LEVEL_LOG_FORMAT, exceptionInfoString, exceptionAuxiliaryDataMessage);

        logOnLevel(loggingLevel,  exceptionLogMessage);
    }

    @Override
    public void logExternalException(final String exceptionClassName, final String exceptionMessage, final Map<String, ?> exceptionAuxiliaryData) {
        if (exceptionClassName == null) {
            return;
        }

        final String exceptionAuxiliaryDataMessage = prepareExceptionAuxiliaryDataMessage(exceptionAuxiliaryData, System.lineSeparator());
        final String exceptionInfoString = String.format(LoggingConstants.EXCEPTION_FULL_LEVEL_MESSAGE_FORMAT, exceptionClassName, exceptionMessage);

        final String exceptionLogMessage = String.format(LoggingConstants.EXCEPTION_FULL_LEVEL_LOG_FORMAT, exceptionInfoString, exceptionAuxiliaryDataMessage);

        final LoggingLevel loggingLevel = fetchConfiguredLoggingLevelForException(exceptionClassName);

        logOnLevel(loggingLevel,  exceptionLogMessage);
    }

    private LoggingVerbosityLevel fetchConfiguredLoggingVerbosityLevelForException(final Exception exception) {
        final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);

        final String messageCode = String.format(LoggingConstants.LOGGING_VERBOSITY_LEVEL_RESOLVING_FORMAT, fetchClassNameForException(exception));
        final String configuredLoggingVerbosityLevel = messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(new String[] { messageCode }, LoggingVerbosityLevel.FULL.name())).toUpperCase();

        try {
            return LoggingVerbosityLevel.valueOf(configuredLoggingVerbosityLevel);
        }
        catch (final IllegalArgumentException ignored) {
            log.warn("Unrecognized verbosity level {} defined for {}", configuredLoggingVerbosityLevel, messageCode);
        }

        return LoggingVerbosityLevel.FULL;
    }

    private LoggingLevel fetchConfiguredLoggingLevelForException(final String exceptionClassName) {
        final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);

        final String messageCode = String.format(LoggingConstants.LOGGING_LEVEL_RESOLVING_FORMAT, exceptionClassName);
        final String loggingLevel = messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(new String[] { messageCode }, LoggingLevel.ERROR.name())).toUpperCase();

        try {
            return LoggingLevel.valueOf(loggingLevel);
        }
        catch (final IllegalArgumentException ignored) {
            log.warn("Unrecognized level {} defined for {}", loggingLevel, messageCode);
        }

        return LoggingLevel.ERROR;
    }

    private String fetchClassNameForException(final Exception exception) {
        if (exception == null) {
            return "";
        }

        return exception.getClass().getName();
    }

    private String fetchMessageForException(final Exception exception) {
        if (exception == null) {
            return "";
        }

        return exception.getMessage();
    }

    private String prepareExceptionAuxiliaryDataMessage(final Map<String, ?> exceptionAuxiliaryData, final String separator) {
        if (exceptionAuxiliaryData == null) {
            return "";
        }

        return exceptionAuxiliaryData.entrySet().stream()
                .map(entry -> String.format(LoggingConstants.AUXILIARY_DATA_FORMAT, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(separator));
    }

    private void logOnLevel(final LoggingLevel loggingLevel, final String message) {
        logOnLevel(loggingLevel, message, null);
    }

    private void logOnLevel(final LoggingLevel loggingLevel, final String message, final Exception exception) {
        switch (loggingLevel) {
            case DEBUG:
                log.debug(message, exception);
                break;
            case INFO:
                log.info(message, exception);
                break;
            case WARN:
                log.warn(message, exception);
                break;
            case ERROR:
                log.error(message, exception);
                break;
        }
    }
}
