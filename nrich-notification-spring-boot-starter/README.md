# nrich-notification-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-notification-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-notification-spring-boot-starter)

## Overview

Spring Boot starter for `nrich-notification` module. The purpose of `nrich-notification` is to provide a unified response format. It parses the messages from `MessageSource` and creates
notifications that can be sent to the client. Detailed validation errors are also provided in a readable form.
Starter module provides a `@Configuration` class (`NrichNotificationAutoConfiguration`) with default configuration of `nrich-notification` module (while allowing for overriding with conditional
annotations)  and does automatic registration through `spring.factories`.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository][Maven Central]. To include the dependency use the following configurations.

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

Note if using `nrich-bom` dependency versions should be omitted.

### Configuration

Configuration is done through a property file, available properties and descriptions are given bellow (all properties are prefixed with nrich.notification which is omitted for readability):

| property            | description                                                                                                      | default value |
|---------------------|------------------------------------------------------------------------------------------------------------------|---------------|
| register-messages   | Whether default messages should be registered (if users want to provide their own messages this can be disabled) | true          |

The default configuration values in yaml format for easier modification are given bellow:

```yaml

nrich.notification:
  register-messages: true

```

The standard notification messages are given in `notificationMessages` file and contain default error, success and info messages that will be returned to the client if no other messages are
configured.

### Using the library

Once added to the classpath if running in the servlet environment the library automatically registers a `@RestControllerAdvice` that handles exceptions and provides a consistent exception response
format. If users want to return all the responses in a standard way they should inject `NotificationResponseService` bean and call one of the methods
available (`responseWithNotificationActionResolvedFromRequest`). Example is given bellow:

```java

@RestController("user")
@RequiredArgsConstructor
public class UserController {

    private final NotificationResponseService<ResponseWithNotification<?>> notificationResponseService;

    private final UserService userService;

    @PostMapping
    public ResponseWithNotification<?> save(CreateUserRequest request) {
        User saved = userService.save(request);

        return notificationResponseService.responseWithNotificationActionResolvedFromRequest(saved);
    }
}


```
