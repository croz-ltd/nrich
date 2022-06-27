# nrich-webmvc

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-webmvc/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-webmvc)

## Overview

`nrich-webmvc` module provides additional functionality built on top of Spring Web MVC framework. Its main purpose is exception handling through
`NotificationErrorHandlingRestControllerAdvice` and providing notification response to client. It also contains additional utility classes that disable binding of transient properties, convert empty
strings to null etc.

## Setting up Spring beans

`nrich-webmvc` module depends on `nrich-logging` and `nrich-notification` modules but users can provide their own implementations of `NotificationResponseService` and `LoggingService`.

```java

@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {

    @Bean
    public TransientPropertyResolverService transientPropertyResolverService() {
        return new DefaultTransientPropertyResolverService();
    }

    @Bean
    public ControllerEditorRegistrationAdvice controllerEditorRegistrationAdvice(TransientPropertyResolverService transientPropertyResolverService) {
        return new ControllerEditorRegistrationAdvice(true, true, transientPropertyResolverService);
    }

    @Bean
    public ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService() {
        return new DefaultExceptionAuxiliaryDataResolverService();
    }

    @Bean
    public ExceptionHttpStatusResolverService exceptionHttpStatusResolverService(MessageSource messageSource) {
        return new MessageSourceExceptionHttpStatusResolverService(messageSource);
    }

    @Bean
    public NotificationErrorHandlingRestControllerAdvice notificationErrorHandlingRestControllerAdvice(BaseNotificationResponseService<?> notificationResponseService, LoggingService loggingService, ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService, ExceptionHttpStatusResolverService exceptionHttpStatusResolverService) {
        return new NotificationErrorHandlingRestControllerAdvice(Collections.singletonList(ExecutionException.class.getName()), Collections.singletonList("uuid"), notificationResponseService, loggingService, exceptionAuxiliaryDataResolverService, exceptionHttpStatusResolverService);
    }

    @Bean
    public ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver() {
        return new ConstrainedSessionLocaleResolver("en", Arrays.asList("en", "hr"));
    }
}

```

`TransientPropertyResolverService` resolves a list of transient properties for type.

`ControllerEditorRegistrationAdvice` converts empty strings to null and disables binding of transient properties (it accepts arguments that decide if binding should be disabled and if empty strings
should be converted to null).

`ExceptionAuxiliaryDataResolverService` resolves additional data for each exception that will be logged and (if configured) sent to client with notification (this can be current time, uuid etc).
Default implementation returns uuid, current time, request uri and request method.

`ExceptionHttpStatusResolverService` resolves http status for each exception. Default implementation `MessageSourceExceptionHttpStatusResolverService` resolves status by using Spring's `MessageSource`.
Message code for resolving is `fullyQualifiedExceptionName.httpStatus`

`NotificationErrorHandlingRestControllerAdvice` is responsible for logging errors, resolving addition data for notifications, creating notification and converting them to response for the client. It
accepts a list of exceptions that contain original exceptions as cause properties (i.e. `ExecutionException`), a list of exception auxiliary data to be included in notification and services that are
used for logging, data resolving and notification creation.

`ConstrainedSessionLocaleResolver` is used when we want to limit locale selection.

## Usage

For usage it is enough to add module as dependency and set up beans. `NotificationErrorHandlingRestControllerAdvice`
handles all exceptions thrown by user code (including binding and validation exceptions) and it uses `LoggingService` to log errors and `NotificationResponseService`
to create notifications that are sent to client in JSON format.

Example response on exception is:

```json

{
    "notification": {
        "title": "Error",
        "content": "Error occurred",
        "messageList": [],
        "severity": "ERROR"
    }
}

```
