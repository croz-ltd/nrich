# nrich-logging-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-logging-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-logging-spring-boot-starter)

## Overview

Spring Boot starter for `nrich-logging` module. The purpose of `nrich-logging` module is to provide a unified and configurable logging format. It is used by `nrich-webmvc` module to log exceptions.
It also allows logging level and logging verbosity (details about exception) to be easily configured through `messages.properties` file.
Starter module provides a `@Configuration` class (`NrichLoggingAutoConfiguration`) with default configuration of `nrich-logging` module (while allowing for overriding with conditional annotations)
and does automatic registration through `spring.factories`.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository](https://search.maven.org/). To include the dependency use the following configurations.

With Maven:

```xml

<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-logging-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>

```

With Gradle:

```groovy

implementation "net.croz.nrich:nrich-logging-spring-boot-starter:${nrich.version}"

```

Note if using `nrich-bom` dependency versions should be omitted.

### Using the module

After adding the dependency a bean of type `LoggingService` is available for dependency injection. The `LoggingService` is used by `NotificationErrorHandlingRestControllerAdvice`
to log uncaught exception but users can also use it to manually log caught exceptions. An example usage is given bellow (more detailed examples are found in `nrich-logging` module
[README.MD](../nrich-logging/README.md)):

```java

@RequiredArgsConstructor
@Service
public class ExampleService {

    private final LoggingService loggingService;

    public void executeAction() {
        try {
            // some operation that can throw an exception
        }
        catch (Exception exception) {
            loggingService.logInternalException(exception, null);
        }
    }
}


```
