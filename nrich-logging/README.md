# nrich-logging

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-logging/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-logging)

## Overview

`nrich-logging` is a module that adds logging service for logging errors in a standard format. Logging service can also resolve verbosity and logging levels for each exception from configuration.

## Setting up Spring beans

Setup is simple, one has to define logging service bean and inject it when common logging functionality (format, level resolving etc.) is required.

```java

@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {

    @Bean
    public LoggingService loggingService(MessageSource messageSource) {
        return new Slf4jLoggingService(messageSource);
    }
}

```

`LoggingService` is responsible for logging exceptions. Default implementation is `Slf4jLoggingService` that uses `Slf4J` logger for logging exceptions and resolves verbosity and logging levels from
Spring's `MessageSource`.

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

`net.croz.TextException.loggingVerbosityLevel` supported values are NONE, COMPACT, FULL

logging level is resolved from following key value:

`net.croz.TextException.loggingLevel` supported values are DEBUG, WARN, INFO, ERROR

When `loggingVerbosityLevel` is set to NONE the logging of the exception is skipped.

When it is set to COMPACT the output is given bellow:

```shell

ERROR net.croz.nrich.logging.service.Slf4jLoggingService - Exception occurred: [className: net.croz.TestException], message: Something went wrong, additionalInfoData:

```

When it is set to FULL the output is given bellow (note that full stacktrace is included):

```shell

09:42:00.841 [main] ERROR net.croz.nrich.logging.service.Slf4jLoggingService - Exception occurred
net.croz.TestException: Something went wrong
	at net.croz.nrich.logging.service.Slf4jLoggingServiceTest.shouldLogOnFullVerbosityLevel(Slf4jLoggingServiceTest.java:121)
	.... (rest of stacktrace is omitted for brevity)
09:42:00.841 [main] ERROR net.croz.nrich.logging.service.Slf4jLoggingService - ---------------- Information about above exception Exception occurred: [className: net.croz.TestException], message: Something went wrong:  ----------------


```
