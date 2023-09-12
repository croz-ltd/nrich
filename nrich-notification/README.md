# nrich-notification

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-notification/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-notification)

## Overview

`nrich-notification` is a module intended for addition of notifications into the server-side response which can be shown on the client-side.
It's main purpose is to provide a unified response format.

It supports three different notification severity levels which are described in table bellow.

| Notification severity | Description                                                                    |
|:---------------------:|--------------------------------------------------------------------------------|
|        `INFO`         | an action was successfully executed such as 'Entity has been saved' or similar |
|       `WARNING`       | validation has failed or similar                                               |
|        `ERROR`        | exception occurred while processing a request                                  |

The module can also send a list of validation errors to the client-side with the original notification.
Notification messages are resolved by message keys from Spring's `MessageSource`.
Keys can be either fixed strings or in case of exceptions their class names.

## Setting up Spring beans

To be able to use this module following bean configuration is required:

```java
@Configuration
public class NrichNotificationConfiguration {

    @Bean
    public ConstraintConversionService constraintConversionService() {
        return new DefaultConstraintConversionService();
    }

    @Bean
    public NotificationMessageResolverService notificationMessageResolverService(MessageSource messageSource) {
        return new MessageSourceNotificationMessageResolverService(messageSource);
    }

    @Bean
    public NotificationResolverService notificationResolverService(NotificationMessageResolverService notificationMessageResolverService, ConstraintConversionService constraintConversionService) {
        return new DefaultNotificationResolverService(notificationMessageResolverService, constraintConversionService);
    }

    @Bean
    public NotificationResponseService notificationResponseService(NotificationResolverService notificationResolverService) {
        return new WebMvcNotificationResponseService(notificationResolverService);
    }

}
```

### ConstraintConversionService

[`ConstraintConversionService`][constraint-conversion-service-url] is responsible for converting `jakarta.validation.ConstraintViolation` list to Spring `Errors` instance.
This conversion is done to make the implementation of [`NotificationResolverService`][notification-resolver-service-url] less complex since it only needs to work with Spring's `Errors` when processing
validation errors.

---

### NotificationMessageResolverService

[`NotificationMessageResolverService`][nrich-notification-message-resolver-service-url] is responsible for resolving messages from message codes and `ObjectErrors`.
Default implementation is [`MessageSourceNotificationMessageResolverService`][message-source-notification-message-resolver-service-url] that resolves messages from Spring's `MessageSource`.

---

### NotificationResolverService

[`NotificationResolverService`][notification-resolver-service-url] is the core service that is responsible for creating notifications.
It can create notifications for validation errors, exceptions or action names.
Consequently, it accepts either Spring's `Errors` or Jakarta `ConstraintViolationException` for creating validation notifications, `Throwable` for creating error notifications and `String` for action
notification.

---

### NotificationResponseService

[`NotificationResponseService`][notification-response-service-url] adds support for additional data in notifications by introducing additional methods that return
[`NotificationDataResponse`][notification-data-response-url].
When using Spring Web MVC users can use [`WebMvcNotificationResponseService`][web-mvc-notification-response-service-url] that is a wrapper around
[`NotificationResolverService`][notification-resolver-service-url] which provides a convenient way of returning a notification with a response.

---

### Default MessageSource setup

`nrich-notification` also includes [`nrich-notification-messages`][nrich-notification-messages-url] property files that contain required default messages that need to be added to
`MessageSource` (this is done automatically when using Spring Boot starter module).

## Usage

Following examples of usage demonstrate `nrich-notification` module's ability to create notifications that respond to **actions**, **exceptions** and **validation errors**.
`nrich-notification` is used as a dependency of [`nrich-webmvc`][nrich-webmvc-url] module and as such is better explained with examples that are based on requests and responses.

### Basic notification structure

Every notification class in `nrich-notification` inherits [`Notification`][notification-url] class that is specified in [`nrich-notification-api`][nrich-notification-api-url] module and has these
attributes:

| Attribute               | Description                                      |
|-------------------------|--------------------------------------------------|
| `title`                 | notification title                               |
| `content`               | notification content                             |
| `messageList`           | list of additional messages                      |
| `severity`              | indicates the importance of notification         |
| `uxNotificationOptions` | additional options that the client can interpret |
| `timestamp`             | notification timestamp                           |

### Exception notifications

If we wish to have unified error notification handling we can define `RestControllerAdvice` where, when handling exceptions with defined exception handlers,
[`NotificationResponseService`][notification-response-service-url] creates notifications that are then sent to the client. Such an advice is already implemented in [`nrich-webmvc`][nrich-webmvc-url].

A simple implementation of former advice would look something like this:

```java
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class NotificationErrorHandlingRestControllerAdvice {

    private final NotificationResponseService notificationResponseService;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public NotificationResponse handleException(Exception exception, HttpServletRequest request) {
        Map<String, Object> exceptionAuxiliaryData = exceptionAuxiliaryData(); // prepare some additional information about exception, in this case UUID
        AdditionalNotificationData notificationData = AdditionalNotificationData.builder().messageListDataMap(exceptionAuxiliaryData).build();

        log.error("Error occurred", exception);

        return notificationResponseService.responseWithExceptionNotification(exception, notificationData);
    }

    private Map<String, Object> exceptionAuxiliaryData() {
        Map<String, Object> exceptionAuxiliaryData = new HashMap<>();

        exceptionAuxiliaryData.put("uuid", UUID.randomUUID().toString());

        return exceptionAuxiliaryData;
    }

}
```

The resulting response to the request that triggered an exception would then be:

```json
{
    "notification": {
        "title": "Error",
        "content": "Error occurred",
        "messageList": [
            "UUID: 4d2aae89-76a9-4768-96e4-e75ea604615e"
        ],
        "severity": "ERROR",
        "timestamp": "2022-07-18T12:09:26.311894Z"
    }
}
```

where `"UUID: 4d2aae89-76a9-4768-96e4-e75ea604615e"` is additional data that we inserted into the notification with optional method argument of type
[`AdditionalNotificationData`][additional-notification-data-url], that we created with [`AdditionalNotificationData`][additional-notification-data-url] builder.

### Action notifications

Users can also use [`NotificationResponseService`][notification-response-service-url] to return notifications with a result from controller actions.

For given request class:

```java
@Getter
@Setter
public class ExampleEntity {

    @NotNull
    private String value;

}
```

when we call `save` method that should save a new instance of `ExampleEntity` class:

```java
@RestController("example")
@RequiredArgsConstructor
public class NotificationTestController {

    private final NotificationResponseService notificationResponseService;

    private final ExampleService exampleService;

    @PostMapping("save")
    public NotificationDataResponse<ExampleEntity> save(@Valid @RequestBody ExampleEntity exampleEntity) {
        ExampleEntity saved = exampleService.save(exampleEntity);

        return notificationResponseService.responseWithNotificationActionResolvedFromRequest(saved);
    }

}
```

the resulting response to the request that executed an action would be:

```json
{
    "notification": {
        "title": "Success",
        "content": "Action has been executed",
        "messageList": [],
        "severity": "INFO",
        "uxNotificationOptions": null,
        "timestamp": "2022-07-18T12:50:24.879571Z"
    },
    "data": {
        "value": "Hello world!"
    }
}
```

### Validation notifications

If `nrich-notification` module is used with [`nrich-webmvc`][nrich-webmvc-url] module, which adds additional exception handlers that cover validation exceptions, `nrich-notification` module will
create a notification for invalid requests that describes which validation constraints where not fulfilled.

For example, if we fail to provide a valid value field for class `ExampleEntity` then this response with error message will be returned:

```json
{
    "notification": {
        "title": "Validation failed",
        "content": "Found validation errors:",
        "messageList": [
            "value: Cannot be null"
        ],
        "severity": "WARNING",
        "uxNotificationOptions": null,
        "timestamp": "2022-07-18T13:14:12.850023Z",
        "validationErrorList": [
            {
                "objectName": "value",
                "errorMessageList": [
                    "value: Cannot be null"
                ]
            }
        ]
    }
}
```

## Custom notification data

If we wish to customize the title and/or content of the notification, a new key-value pair should be added to the `messages.properties` file.
Default implementation of [`NotificationResolverService`][notification-resolver-service-url] interface ([`DefaultNotificationResolverService`][default-notification-resolver-service-url]) uses the
Spring's `MessageSource` to read the wanted value for the specified key from the `message.properties` file.

Messages are resolved by matching the given **action codes** to the keys in the `message.properties` file.
In [`DefaultNotificationResolverService`][default-notification-resolver-service-url] title and content of the notification is resolved from following keys:

- `actionCode.title`
- `actionCode.content`

### Custom exception notification data

Depending on the context of notification creation, if a notification is created while resolving an exception, then the action code is a **fully qualified class name for that exception**.

For example, let's say we have this exception handler:

```java
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class NotificationErrorHandlingRestControllerAdvice {

    private final NotificationResponseService notificationResponseService;

    @ExceptionHandler(CustomExampleException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public NotificationResponse handleCustomException(CustomExampleException exception, HttpServletRequest request) {
        log.error("Custom error occurred", exception);

        return notificationResponseService.responseWithExceptionNotification(exception);
    }

}
```

and we have this key-value pair in the `messages.properties` file:

```properties
example.CustomExampleException.title=Custom exception title
example.CustomExampleException.content=Custom exception content
```

Then the response will be:

```json
{
    "notification": {
        "title": "Custom exception title",
        "content": "Custom exception content",
        "messageList": [],
        "severity": "ERROR",
        "uxNotificationOptions": null,
        "timestamp": "2022-07-19T07:59:50.850622Z"
    }
}
```

### Custom action notification data

Different to exception notification data, action notification's action code is a **fixed string**.

There are two ways to provide the action code to the notification service when creating action notification data:

- manually, by providing the code ourselves
- automatically, by resolving the code from the request path

#### Manual providing

Manual providing of action code is recommended when automatic resolving from the request path is not unique, such as routes with parameters.

Example of manual providing:

```java
@RequiredArgsConstructor
@RequestMapping("notification-example")
@RestController
public class ExampleController {

    private final NotificationResponseService notificationResponseService;

    @GetMapping("manual")
    public NotificationResponse manualExample() {
        return notificationResponseService.responseWithNotification("manual.example");
    }
}
```

With `messages.properties` file set as this:

```properties
manual.example.title=Manual title
manual.example.content=Manual content
```

response is:

```json
{
    "notification": {
        "title": "Manual title",
        "content": "Manual content",
        "messageList": [],
        "severity": "INFO",
        "uxNotificationOptions": null,
        "timestamp": "2022-07-19T08:22:01.979151Z"
    }
}
```

#### Automatic resolving

Automatic resolving of the notification action code was used in chapter describing the action notification. Method `responseWithNotificationActionResolvedFromRequest` will resolve the action code from
current request and HTTP method and the key for resolving title and content is: `notification-example.save.post`.

Example of automatic resolving:

```java
@RequiredArgsConstructor
@RequestMapping("notification-example")
@RestController
public class ExampleController {

    private final NotificationResponseService notificationResponseService;

    @PostMapping("save")
    public NotificationResponse automaticExample() {
        return notificationResponseService.responseWithNotificationActionResolvedFromRequest();
    }
}
```

If we were to put these key-value pairs into the `messages.properties` file:

```properties
notification-example.save.post.title=Automatic resolution title
notification-example.save.post.content=Automatic resolution content
```

then the response that we would receive would be different from the one already seen:

```json
{
    "notification": {
        "title": "Automatic resolution title",
        "content": "Automatic resolution content",
        "messageList": [],
        "severity": "INFO",
        "uxNotificationOptions": null,
        "timestamp": "2022-07-19T08:33:32.800894Z"
    },
    "data": {
        "value": "Hello there!"
    }
}
```

### Custom validation notification data

For validation failure notifications, content and title are always resolved from fixed strings:

- `notification.validation-failed.title`
- `notification.validation-failed.content`

Validation failure errors are added as messages to notification instances and their failure messages can also be customized.

For example, class situated in package `example`:

```java
package example;

@Getter
@Setter
public class ExampleEntity {

    @NotNull
    private String value;

}
```

When validation fails for instance of class `ExampleEntity` on field `value`, validation failure messages are resolved from following action codes (listed by their priority):

- `example.ExampleEntity.value.NotNull.invalid`
- `exampleEntity.value.NotNull.invalid`
- `value.NotNull.invalid`
- `NotNull.invalid`

### AdditionalNotificationData

Additional notification data can be specified using [`AdditionalNotificationData`][additional-notification-data-url].
It can be used to override notification severity, add additional messages and specify ux notification options.
Messages defined in the `messageListDataMap` are added to the notification into the `messageList` field as messages where the message text is resolved from the action code:

`notification.additional-data.mapKey.message`

where `mapKey` is a key from `messageListDataMap` and value is passed in as argument for resolving a message.

As such, if we have `messageListDataMap` with key `UUID`, then if we set property file like this:

```properties
notification.additional-data.uuid.message=UUID example data is: {0}
```

notification response will be:

```json
{
    "notification": {
        "title": "Error",
        "content": "Error occurred",
        "messageList": [
            "UUID example data is: 2162225c-cdf8-45cd-b579-b3389f464aa0"
        ],
        "severity": "ERROR",
        "uxNotificationOptions": null,
        "timestamp": "2022-07-19T09:01:31.178565Z"
    }
}
```

where we can see that instead of `{0}` the value was interpolated.

[//]: # (Reference links)

[nrich-notification-api-url]: ../nrich-notification-api/README.md

[nrich-webmvc-url]: ../nrich-webmvc/README.md

[constraint-conversion-service-url]: ../nrich-notification-api/src/main/java/net/croz/nrich/notification/api/service/ConstraintConversionService.java

[nrich-notification-message-resolver-service-url]: ../nrich-notification-api/src/main/java/net/croz/nrich/notification/api/service/NotificationMessageResolverService.java

[message-source-notification-message-resolver-service-url]: ../nrich-notification/src/main/java/net/croz/nrich/notification/service/MessageSourceNotificationMessageResolverService.java

[notification-resolver-service-url]: ../nrich-notification-api/src/main/java/net/croz/nrich/notification/api/service/NotificationResolverService.java

[notification-response-service-url]: ../nrich-notification-api/src/main/java/net/croz/nrich/notification/api/service/NotificationResponseService.java

[web-mvc-notification-response-service-url]: ../nrich-notification/src/main/java/net/croz/nrich/notification/service/WebMvcNotificationResponseService.java

[notification-data-response-url]: ../nrich-notification-api/src/main/java/net/croz/nrich/notification/api/response/NotificationDataResponse.java

[nrich-notification-messages-url]: ../nrich-notification/src/main/resources/nrich-notification-messages.properties

[notification-url]: ../nrich-notification-api/src/main/java/net/croz/nrich/notification/api/model/Notification.java

[additional-notification-data-url]: ../nrich-notification-api/src/main/java/net/croz/nrich/notification/api/model/AdditionalNotificationData.java

[default-notification-resolver-service-url]: ../nrich-notification/src/main/java/net/croz/nrich/notification/service/DefaultNotificationResolverService.java
