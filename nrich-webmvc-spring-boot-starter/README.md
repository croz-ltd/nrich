# nrich-webmvc-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-webmvc-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-webmvc-spring-boot-starter)

## Overview

Spring Boot starter for `nrich-webmvc` module. The main purpose of `nrich-webmvc` module is to provide an `@RestController` advice that handles exceptions. Exceptions are logged using `nrich-logging`
module and a notification is created using `nrich-notification` module. Other useful features include providing a way of adding additional data to responses by implementing
`ExceptionAuxiliaryDataResolverService` (default implementation is provided), resolving status of exceptions, limiting locale selection, ignoring serialization of transient properties and converting
empty strings to null. Starter module provides a `@Configuration` class (`NrichWebMvcAutoConfiguration`) with default configuration of `nrich-webmvc` module
(while allowing for overriding with conditional annotations) and `@ConfigurationProperties` class (`NrichWebMvcProperties`) with default configured values
and does automatic registration through `spring.factories`.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository][Maven Central]. To include the dependency use the following configurations.

With Maven:

```xml

<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-webmvc-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>

```

With Gradle:

```groovy

implementation "net.croz.nrich:nrich-webmvc-spring-boot-starter:${nrich.version}"

```

Note if using `nrich-bom` dependency versions should be omitted.

### Configuration

Configuration is done through a property file, available properties and descriptions are given bellow (all properties are prefixed with nrich.webmvc which is omitted for readability):

| property                                            | description                                                                    | default value                           |
|-----------------------------------------------------|--------------------------------------------------------------------------------|-----------------------------------------|
| controller-advice-enabled                           | Whether controller advice that handles exceptions is enabled                   | true                                    |
| exception-auxiliary-data-resolving-enabled          | Whether default `ExceptionAuxiliaryDataResolverService` is enabled             | true                                    |
| convert-empty-strings-to-null                       | Whether empty strings should be converted to null when binding requests        | true                                    |
| ignore-transient-fields                             | Whether transient fields should be ignored when binding requests               | true                                    |
| exception-to-unwrap-list                            | List of exceptions for which the cause will be used when resolving messages    | java.util.concurrent.ExecutionException |
| exception-auxiliary-data-to-include-in-notification | List of exception auxiliary data to be included in notification sent to client | uuid                                    |
| default-locale                                      | Default locale                                                                 | null                                    |
| allowed-locale-list                                 | A list of locales users are allowed to set                                     | null                                    |

The default configuration values in yaml format for easier modification are given bellow:

```yaml

nrich.webmvc:
  controller-advice-enabled: true
  exception-auxiliary-data-resolving-enabled : true
  convert-empty-strings-to-null : true
  ignore-transient-fields: true
  exception-to-unwrap-list: java.util.concurrent.ExecutionException
  exception-auxiliary-data-to-include-in-notification: uuid
  default-locale:
  allowed-locale-list:

```

### Using the library

After adding the dependency and adjusting the properties if necessary the library is active by default. If a custom message and/or status needs to be registered for an exception it is done using
message.properties. Messages for exceptions are searched by class name.
Bellow an example is given on how to structure messages for `net.croz.nrich.notification.NotificationResolverServiceTestException`:

```properties

net.croz.nrich.notification.NotificationResolverServiceTestException.content=Error message
net.croz.nrich.notification.NotificationResolverServiceTestException.severity=WARNING
net.croz.nrich.notification.NotificationResolverServiceTestException.title=Custom error title
net.croz.nrich.notification.NotificationResolverServiceTestException.httpStatus=400

```
