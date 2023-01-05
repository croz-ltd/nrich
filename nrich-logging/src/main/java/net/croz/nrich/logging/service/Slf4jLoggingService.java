/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
    public void logInternalException(Exception exception, Map<String, ?> exceptionAuxiliaryData) {
        LoggingVerbosityLevel configuredLoggingVerbosityLevelForException = fetchConfiguredLoggingVerbosityLevelForException(exception);

        if (configuredLoggingVerbosityLevelForException == LoggingVerbosityLevel.COMPACT) {
            logInternalExceptionAtCompactVerbosityLevel(exception, exceptionAuxiliaryData);
        }
        else if (configuredLoggingVerbosityLevelForException == LoggingVerbosityLevel.FULL) {
            logInternalExceptionAtFullVerbosityLevel(exception, exceptionAuxiliaryData);
        }
    }

    @Override
    public void logInternalExceptionAtCompactVerbosityLevel(Exception exception, Map<String, ?> exceptionAuxiliaryData) {
        String exceptionAuxiliaryDataMessage = prepareExceptionAuxiliaryDataMessage(exceptionAuxiliaryData, ", ");
        String className = fetchClassNameForException(exception);
        String message = fetchMessageForException(exception);
        String logMessage = String.format(LoggingConstants.EXCEPTION_COMPACT_LEVEL_LOG_FORMAT, className, message, exceptionAuxiliaryDataMessage);
        LoggingLevel loggingLevel = fetchConfiguredLoggingLevelForException(className);

        logOnLevel(loggingLevel, logMessage);
    }

    @Override
    public void logInternalExceptionAtFullVerbosityLevel(Exception exception, Map<String, ?> exceptionAuxiliaryData) {
        String className = fetchClassNameForException(exception);
        String message = fetchMessageForException(exception);
        String exceptionInfoString = String.format(LoggingConstants.EXCEPTION_FULL_LEVEL_MESSAGE_FORMAT, className, message);
        LoggingLevel loggingLevel = fetchConfiguredLoggingLevelForException(className);

        logOnLevel(loggingLevel, "Exception occurred", exception);

        String exceptionAuxiliaryDataMessage = prepareExceptionAuxiliaryDataMessage(exceptionAuxiliaryData, System.lineSeparator());
        String exceptionLogMessage = String.format(LoggingConstants.EXCEPTION_FULL_LEVEL_LOG_FORMAT, exceptionInfoString, exceptionAuxiliaryDataMessage);

        logOnLevel(loggingLevel, exceptionLogMessage);
    }

    @Override
    public void logExternalException(String exceptionClassName, String exceptionMessage, Map<String, ?> exceptionAuxiliaryData) {
        if (exceptionClassName == null) {
            return;
        }

        String exceptionAuxiliaryDataMessage = prepareExceptionAuxiliaryDataMessage(exceptionAuxiliaryData, System.lineSeparator());
        String exceptionInfoString = String.format(LoggingConstants.EXCEPTION_FULL_LEVEL_MESSAGE_FORMAT, exceptionClassName, exceptionMessage);

        String exceptionLogMessage = String.format(LoggingConstants.EXCEPTION_FULL_LEVEL_LOG_FORMAT, exceptionInfoString, exceptionAuxiliaryDataMessage);

        LoggingLevel loggingLevel = fetchConfiguredLoggingLevelForException(exceptionClassName);

        logOnLevel(loggingLevel, exceptionLogMessage);
    }

    private LoggingVerbosityLevel fetchConfiguredLoggingVerbosityLevelForException(Exception exception) {
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
        String className = fetchClassNameForException(exception);
        String messageCode = String.format(LoggingConstants.LOGGING_VERBOSITY_LEVEL_RESOLVING_FORMAT, className);
        String configuredLoggingVerbosityLevel = messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(new String[] { messageCode }, LoggingVerbosityLevel.FULL.name())).toUpperCase();

        try {
            return LoggingVerbosityLevel.valueOf(configuredLoggingVerbosityLevel);
        }
        catch (IllegalArgumentException ignored) {
            log.warn("Unrecognized verbosity level {} defined for {}", configuredLoggingVerbosityLevel, messageCode);
        }

        return LoggingVerbosityLevel.FULL;
    }

    private LoggingLevel fetchConfiguredLoggingLevelForException(String exceptionClassName) {
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);

        String messageCode = String.format(LoggingConstants.LOGGING_LEVEL_RESOLVING_FORMAT, exceptionClassName);
        String loggingLevel = messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(new String[] { messageCode }, LoggingLevel.ERROR.name())).toUpperCase();

        try {
            return LoggingLevel.valueOf(loggingLevel);
        }
        catch (IllegalArgumentException ignored) {
            log.warn("Unrecognized level {} defined for {}", loggingLevel, messageCode);
        }

        return LoggingLevel.ERROR;
    }

    private String fetchClassNameForException(Exception exception) {
        if (exception == null) {
            return "";
        }

        return exception.getClass().getName();
    }

    private String fetchMessageForException(Exception exception) {
        if (exception == null) {
            return "";
        }

        return exception.getMessage();
    }

    private String prepareExceptionAuxiliaryDataMessage(Map<String, ?> exceptionAuxiliaryData, String separator) {
        if (exceptionAuxiliaryData == null) {
            return "";
        }

        return exceptionAuxiliaryData.entrySet().stream()
            .map(entry -> String.format(LoggingConstants.AUXILIARY_DATA_FORMAT, entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(separator));
    }

    private void logOnLevel(LoggingLevel loggingLevel, String message) {
        logOnLevel(loggingLevel, message, null);
    }

    private void logOnLevel(LoggingLevel loggingLevel, String message, Exception exception) {
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
