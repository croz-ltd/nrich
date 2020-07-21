# nrich-webmvc

## Overview

nrich-webmvc library provides additional functionality built on top of Spring Web MVC framework. Its main purpose is exception handling through
`NotificationErrorHandlingRestControllerAdvice` and providing notification response to client. It also contains additional utility
classes that disable binding of transient properties, convert empty strings to null etc.

nrich-webmvc

## Setting up Spring beans

nrich-webmvc depends on nrich-logging and nrich-notification libraries but users can provide their own implementations of `NotificationResponseService` and `LoggingService`.

```


    @Bean
    public TransientPropertyResolverService transientPropertyResolverService() {
       return new DefaultTransientPropertyResolverService();
    }

    @Bean
    public ControllerEditorRegistrationAdvice controllerEditorRegistrationAdvice(final TransientPropertyResolverService transientPropertyResolverService) {
        return new ControllerEditorRegistrationAdvice(true, true, transientPropertyResolverService);
    }

    @Bean
    public ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService() {
        return new DefaultExceptionAuxiliaryDataResolverService();
    }

    @Bean
    public NotificationErrorHandlingRestControllerAdvice notificationErrorHandlingRestControllerAdvice(final NotificationResponseService<?> notificationResponseService, final LoggingService loggingService, final ExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService) {
        return new NotificationErrorHandlingRestControllerAdvice(Collections.singletonList(ExecutionException.class.getName()), Collections.singletonList("uuid"), notificationResponseService, loggingService, exceptionAuxiliaryDataResolverService);
    }

    @Bean
    public ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver() {
        return new ConstrainedSessionLocaleResolver("en", Arrays.asList("en", "hr));
    }



```

`TransientPropertyResolverService` resolves a list of transient properties for type.

`ControllerEditorRegistrationAdvice` converts empty strings to null and disables binding of transient properties (it accepts arguments that decide if binding should be disabled and if empty strings should be converted to null).

`ExceptionAuxiliaryDataResolverService` resolves additional data for each exception that will be logged and (if configured) sent to client with notification (this can be current time, uuid etc).
Default implementation returns uuid, current time, request uri and request method. 

`ConstrainedSessionLocaleResolver` is used when we want to limit locale selection.

## Usage

For usage it is enough to add library as dependency and set up beans.
