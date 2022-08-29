# nrich-notification-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-notification-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-notification-spring-boot-starter)

## Overview

This module is a Spring Boot starter for the [`nrich-notification`][nrich-notification-url] module.
The purpose of the [`nrich-notification`][nrich-notification-url] module is to provide a unified response format by parsing messages from `MessageSource` and creating notifications that can be sent
to the client. Detailed validation errors are also provided in a readable format.

Starter module provides a `@Configuration` class ([`NrichNotificationAutoConfiguration`][nrich-notification-auto-configuration-url]) with default configuration of
[`nrich-notification`][nrich-notification-url] module and does automatic registration of required beans via `spring.factories`.
The configuration class permits overriding with the help of conditional annotations.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository](https://search.maven.org/). To include the dependency use the following configurations.

With Maven:

```xml
<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-notification-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>
```

With Gradle:

```groovy
implementation "net.croz.nrich:nrich-notification-spring-boot-starter:${nrich.version}"
```

Note if using [`nrich-bom`][nrich-bom-url] dependency versions should be omitted.

### Configuration

The configuration is done through a property file.
Available properties and descriptions are given bellow (all properties are prefixed with **nrich.notification** which is omitted for readability):

| property            | description                                                                                                      | default value |
|---------------------|------------------------------------------------------------------------------------------------------------------|---------------|
| register-messages   | whether default messages should be registered (if users want to provide their own messages this can be disabled) | true          |

The default configuration values are given bellow in a yaml format for easier modification:

```yaml
nrich.notification:
    register-messages: true
```

The standard notification messages are given in the [`nrich-notification-messages`][nrich-notification-messages-url] property file which contains default error, success and info messages that will be
returned to the client if no other messages are configured.

### Using the module

When added to the classpath, and if running in the servlet environment the module automatically registers a `@RestControllerAdvice` that handles exceptions and provides a consistent exception response
format. If users want to return all the responses in a standard way they should inject [`NotificationResponseService`][notification-response-service-url] bean and call one of the methods available
(for example `responseWithNotificationActionResolvedFromRequest`).

An example is given bellow:

```java
@RequiredArgsConstructor
@RestController("user")
public class UserController {

    private final WebMvcNotificationResponseService notificationResponseService;

    private final UserService userService;

    @PostMapping
    public NotificationDataResponse<?> save(CreateUserRequest request) {
        User saved = userService.save(request);

        return notificationResponseService.responseWithNotificationActionResolvedFromRequest(saved);
    }

}
```

[//]: # (Reference links)

[nrich-notification-url]: ../nrich-notification/README.md

[nrich-bom-url]: ../nrich-bom/README.md

[nrich-notification-auto-configuration-url]: ../nrich-notification-spring-boot-starter/src/main/java/net/croz/nrich/notification/starter/configuration/NrichNotificationAutoConfiguration.java

[nrich-notification-messages-url]: ../nrich-notification/src/main/resources/nrich-notification-messages.properties

[notification-response-service-url]: ../nrich-notification-api/src/main/java/net/croz/nrich/notification/api/service/NotificationResponseService.java
