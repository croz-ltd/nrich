# nrich-notification

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-notification/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-notification)

## Overview

`nrich-notification` is a module intended for creation of notifications on server side which are shown on the client side. It supports info (i.e. 'Entity has been saved'), warning (i.e. 'Validation
failed') and error (i.e. 'Exception occurred') notification severity levels, and can also send a list of validation errors to client side along with original notification. Notification messages are
resolved by message keys from Spring's `MessageSource`. Keys can be either fixed strings or in case of exceptions their class names.

## Setting up Spring beans

To be able to use this module following configuration is required:

```

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

`ConstraintConversionService` is a service responsible for converting `javax.validation.ConstraintViolation` list to Spring `Errors`
instances making implementation of `NotificationResolverService` less complex since it only needs to work with Spring's `Errors` when processing validation errors.

`NotificationMessageResolverService` is a service responsible for resolving messages from message codes and
`ObjectErrors`. Default implementation is `MessageSourceNotificationMessageResolverService` that resolves messages from Spring's `MessageSource`.

`NotificationResolverService` is main service that is responsible for creating notifications. It can create notifications for validation errors, exceptions or action names.
Consequently, it accepts either Spring's `Errors` or Jakarta `ConstraintViolationException` for creating validation notifications, `Throwable` for creating error notifications and
`String` for action notification.

`NotificationResponseService` is a service that adds support for returning data with notifications by introducing additional methods that return `NotificationDataResponse`.

When using Spring Web MVC users can use `NotificationResponseService` implementation `WebMvcNotificationResponseService` that is a wrapper around `NotificationResolverService` providing a
convenient way of returning a notification with a response.

## Usage

`nrich-notification` is used as a dependency of `nrich-webmvc` module. It is used in a `RestControllerAdvice` and on exceptions it creates notifications that are then sent to the client,
so a common usage would be to use it in a `ControllerAdvice` to have unified error notification handling.
A simple implementation of former advice would look something like this:

```java

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class NotificationErrorHandlingRestControllerAdvice {

    private final NotificationResponseService notificationResponseService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception exception, HttpServletRequest request) {
        Map<String, Object> exceptionAuxiliaryData = exceptionAuxiliaryData();
        AdditionalNotificationData notificationData = AdditionalNotificationData.builder().messageListData(exceptionAuxiliaryData).build())

        log.error("Error occurred", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(notificationResponseService.responseWithExceptionNotification(exception, notificationData);
    }

    private Map<String, Object> exceptionAuxiliaryData() {
        Map<String, Object> exceptionAuxiliaryData = new HashMap<>();

        exceptionAuxiliaryData.put("uuid", UUID.randomUUID().toString());

        return exceptionAuxiliaryData;
    }
}


```

Users can also use `NotificationResponseService` to return notifications with result from controller actions (i.e. 'Entity has been saved' notification with actual entity):

```java

@RestController("example")
@RequiredArgsConstructor
public class NotificationTestController {

    private final NotificationResponseService notificationResponseService;

    private final ExampleService exampleService;

    @PostMapping("save")
    public ResponseWithNotification<?> save(ExampleEntity exampleEntity) {
        ExampleEntity saved = exampleService.save(exampleEntity);

        return notificationResponseService.responseWithNotificationActionResolvedFromRequest(saved);
    }
}


```

in this case a notification message code is resolved from current request and code for resolving title and content is: `example.save`. Users can also provide notification code manually.

In default implementation of `NotificationResolverService` (`DefaultNotificationResolverService`) notification data is resolved from following keys:

title is resolved from code: `actionCode.title`

content is resolved from code: `actionCode.content`

Action code is fully qualified class name when resolving notification for exception (i.e. `net.croz.nrich.notification.stub.NotificationResolverServiceTestException`) or a fixed string. For validation
failure notifications content and title are resolved always from fixed strings:

`notification.validation-failed.title` and `notification.validation-failed.content`

Validation failure errors are added as messages to notification instances. When validation fails for `net.croz.nrich.notification.stub.NotificationResolverServiceTestRequest` instance on field  `name`
with constraint `NotNull` validation failure messages are resolved from following codes:

- `net.croz.nrich.notification.stub.NotificationResolverServiceTestRequest.name.NotNull.invalid`
- `notificationResolverServiceTestRequest.name.NotNull.invalid`
- `name.NotNull.invalid`
- `NotNull.invalid`

Additional notification data (like overridden severity, additional messages, ux notification options) can be specified using `AdditionalNotificationData`. Messages defined in `messageListDataMap` are
added to notification as messages where message text is resolved from key: `notification.additional-data.mapKey.message` where `mapKey`
is key from  `messageListDataMap` and value is passed in as argument for resolving message.

Notification also includes `notificationMessages` files that contain required messages and should be added to `MessageSource` (this is done automatically when using Spring Boot starter).
