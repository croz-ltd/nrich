# nrich-problem-detail-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-problem-detail-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-problem-detail-spring-boot-starter)

## Overview

This module is a Spring Boot starter for the [`nrich-problem-detail`][nrich-problem-detail-url] module.
The purpose of the [`nrich-problem-detail`][nrich-problem-detail-url] module is to provide unified, Spring-native error handling for Spring Web MVC based on
[RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) (`ProblemDetail`), enriched with nrich extensions (`code`, `severity`, `errorId`, `timestamp` and structured validation `errors`).

Starter module provides a `@Configuration` class ([`NrichProblemDetailAutoConfiguration`][nrich-problem-detail-auto-configuration-url]) with default configuration of
[`nrich-problem-detail`][nrich-problem-detail-url] module (registering the exception handler, the default contributors, the validation error resolving service and the default message bundle) and does
automatic registration of required beans. The configuration class permits overriding with the help of conditional annotations.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository](https://search.maven.org/). To include the dependency use the following configurations.

With Maven:

```xml
<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-problem-detail-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>
```

With Gradle:

```groovy
implementation "net.croz.nrich:nrich-problem-detail-spring-boot-starter:${nrich.version}"
```

Note if using [`nrich-bom`][nrich-bom-url] dependency versions should be omitted.

### Configuration

The configuration is done through a property file.
Available properties and descriptions are given bellow (all properties are prefixed with **nrich.problem-detail** which is omitted for readability):

| property                              | description                                                                                                                                          | default value                            |
|---------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------|
| enabled                               | whether the nrich ProblemDetail exception handler is registered                                                                                      | true                                     |
| logging-service-registration-enabled  | whether nrich registers a `LoggingService` that also resolves `problemDetail.nrich.logging.*` keys (set to false to keep the default logging service) | true                                     |
| register-messages                     | whether the default `nrich-problem-detail-messages` bundle is registered into the `MessageSource`                                                    | true                                     |
| exception-to-unwrap-list              | fully qualified names of wrapper exceptions whose cause should be unwrapped before handling                                                          | java.util.concurrent.ExecutionException  |
| include-rejected-value                | whether the rejected value is included in each `errors[]` entry (off by default so submitted values such as passwords/PII are not echoed back)        | false                                    |
| fallback-to-class-name                | whether the `code` extension falls back to the exception's fully qualified class name when no `problemDetail.nrich.code.<fqcn>` message and no `ExceptionWithMessageCode` apply | false          |
| contributor.errors                    | whether the validation `errors[]` contributor is enabled                                                                                             | true                                     |
| contributor.code                      | whether the `code` contributor is enabled                                                                                                            | true                                     |
| contributor.severity                  | whether the `severity` contributor is enabled                                                                                                        | true                                     |
| contributor.error-id                  | whether the `errorId` contributor is enabled                                                                                                         | true                                     |
| contributor.timestamp                 | whether the `timestamp` contributor is enabled                                                                                                       | true                                     |

The default configuration values are given bellow in a yaml format for easier modification:

```yaml
nrich.problem-detail:
    enabled: true
    logging-service-registration-enabled: true
    register-messages: true
    exception-to-unwrap-list:
        - java.util.concurrent.ExecutionException
    include-rejected-value: false
    fallback-to-class-name: false
    contributor:
        errors: true
        code: true
        severity: true
        error-id: true
        timestamp: true
```

The default titles and details are given in the [`nrich-problem-detail-messages`][nrich-problem-detail-messages-url] property file (with a Croatian `_hr` variant) which is registered into the
`MessageSource` unless `register-messages` is set to false.

### Using the module

When added to the classpath, and if running in the servlet environment, the module automatically registers a `@RestControllerAdvice`
([`NrichProblemDetailExceptionHandler`][nrich-problem-detail-exception-handler-url]) that converts exceptions into `application/problem+json` responses. No controller-side wiring is required — throwing an
exception is enough.

Per-exception title, detail, type, code, severity and status are customized through message keys (for example `problemDetail.title.<fqcn>` or `problemDetail.nrich.status.<fqcn>`), and exceptions may carry their
own detail by implementing the `nrich-core-api` `ExceptionWith*` markers. See the [`nrich-problem-detail`][nrich-problem-detail-url] module README for the full list of keys and example responses.

[//]: # (Reference links)

[nrich-problem-detail-url]: ../nrich-problem-detail/README.md

[nrich-bom-url]: ../nrich-bom/README.md

[nrich-problem-detail-auto-configuration-url]: ../nrich-problem-detail-spring-boot-starter/src/main/java/net/croz/nrich/problemdetail/starter/configuration/NrichProblemDetailAutoConfiguration.java

[nrich-problem-detail-exception-handler-url]: ../nrich-problem-detail/src/main/java/net/croz/nrich/problemdetail/handler/NrichProblemDetailExceptionHandler.java

[nrich-problem-detail-messages-url]: ../nrich-problem-detail/src/main/resources/nrich-problem-detail-messages.properties
