# nrich-logging

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-logging/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-logging)

## Overview

nrich-logging is a library that adds logging service for logging errors in a standard format. Logging service can also resolve verbosity and logging levels for each exception from configuration.

## Setting up Spring beans

Setup is simple, one only has to define a single bean and then when common logging functionality (format, level resolving etc.) is required inject that bean.

```
    @Bean
    public LoggingService loggingService(final MessageSource messageSource) {
        return new Slf4jLoggingService(messageSource);
    }

```

`LoggingService` is responsible for logging exceptions. Default implementation is `Slf4jLoggingService` that uses `Slf4J` logger for logging exceptions and resolves verbosity and logging levels from
Springs `MessageSource`.

## Usage

`LoggingService` interface has four methods, default behaviour (implemented in `Slf4jLoggingService`) is described here:

- `void logInternalException(Exception exception, Map<String, ?> exceptionAuxiliaryData)`

Logs exception with optional auxiliary data with verbosity level and logging level resolved from message source.

- `void logInternalExceptionAtCompactVerbosityLevel(Exception exception, Map<String, ?> exceptionAuxiliaryData)`

Logs exception with optional auxiliary data at compact verbosity level and logging level resolved from message source.

- `void logInternalExceptionAtFullVerbosityLevel(Exception exception, Map<String, ?> exceptionAuxiliaryData)`

Logs exception with optional auxiliary data at full verbosity level and logging level resolved from message source.

- `void logExternalException(String exceptionClassName, String exceptionMessage, Map<String, ?> exceptionAuxiliaryData)`

Logs exceptions with class name and optional auxiliary data with logging level resolved from message source (useful when we don't have the exception class on classpath).

For example for exception: net.croz.TextException

verbosity level is resolved from following key value:

`net.croz.TextException.verbosityLevel` supported values are NONE, COMPACT, FULL

logging level is resolved from following key value:

`net.croz.TextException.loggingLevel` supported values are DEBUG, WARN, INFO, ERROR
