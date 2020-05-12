package net.croz.nrich.logging.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.logging.model.LoggingVerbosityLevel;
import net.croz.nrich.logging.service.LoggingService;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class LoggingServiceImpl implements LoggingService {

    private static final String EXCEPTION_COMPACT_LEVEL_LOG_FORMAT = "Exception occurred: [className: %s], message: %s, additionalInfoData: %s";

    private static final String EXCEPTION_FULL_LEVEL_MESSAGE_FORMAT = "Exception occurred: [className: %s], message: %s";

    private static final String EXCEPTION_FULL_LEVEL_LOG_FORMAT = "---------------- Information about above exception %s: %s ----------------";

    private static final String AUXILIARY_DATA_FORMAT = "%s: %s";

    private static final String LOGGING_VERBOSITY_LEVEL_RESOLVING_FORMAT = "%s.loggingVerbosityLevel";

    private static final String UNKNOWN_VALUE_MESSAGE = "UNKNOWN";

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
        final String logMessage = String.format(EXCEPTION_COMPACT_LEVEL_LOG_FORMAT, fetchClassNameForException(exception), fetchMessageForException(exception), exceptionAuxiliaryDataMessage);

        log.error(logMessage);
    }

    @Override
    public void logInternalExceptionAtFullVerbosityLevel(final Exception exception, final Map<String, ?> exceptionAuxiliaryData) {
        final String exceptionInfoString = String.format(EXCEPTION_FULL_LEVEL_MESSAGE_FORMAT, fetchClassNameForException(exception), fetchMessageForException(exception));

        log.error("Exception occurred", exception);

        final String exceptionAuxiliaryDataMessage = prepareExceptionAuxiliaryDataMessage(exceptionAuxiliaryData, System.lineSeparator());
        final String exceptionLogMessage = String.format(EXCEPTION_FULL_LEVEL_LOG_FORMAT, exceptionInfoString, exceptionAuxiliaryDataMessage);

        log.error(exceptionLogMessage);
    }

    @Override
    public void logExternalException(final String exceptionClassName, final String exceptionMessage, final Map<String, ?> exceptionAuxiliaryData) {
        if (exceptionClassName == null) {
            return;
        }

        final String exceptionInfoString = String.format(EXCEPTION_FULL_LEVEL_MESSAGE_FORMAT, exceptionClassName, exceptionMessage);

        final String exceptionAuxiliaryDataMessage = prepareExceptionAuxiliaryDataMessage(exceptionAuxiliaryData, System.lineSeparator());
        final String exceptionLogMessage = String.format(EXCEPTION_FULL_LEVEL_LOG_FORMAT, exceptionInfoString, exceptionAuxiliaryDataMessage);

        log.error(exceptionLogMessage);
    }

    private LoggingVerbosityLevel fetchConfiguredLoggingVerbosityLevelForException(final Exception exception) {
        final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);

        final String messageCode = String.format(LOGGING_VERBOSITY_LEVEL_RESOLVING_FORMAT, fetchConfiguredLoggingVerbosityLevelForException(exception));
        final String configuredLoggingVerbosityLevel = messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(new String[] { messageCode }, UNKNOWN_VALUE_MESSAGE)).toUpperCase();

        try {
            if (!UNKNOWN_VALUE_MESSAGE.equals(configuredLoggingVerbosityLevel)) {
                return LoggingVerbosityLevel.valueOf(configuredLoggingVerbosityLevel);
            }
        }
        catch (final IllegalArgumentException ignored) {
            log.warn("Unrecognized verbosity level {}", configuredLoggingVerbosityLevel);
        }

        return LoggingVerbosityLevel.FULL;
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

        return exceptionAuxiliaryData.entrySet().stream().map(entry -> String.format(AUXILIARY_DATA_FORMAT, entry.getKey(), entry.getValue())).collect(Collectors.joining(separator));
    }
}
