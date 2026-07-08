# nrich-problem-detail

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-problem-detail/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-problem-detail)

## Overview

`nrich-problem-detail` is a module that provides unified, Spring-native error handling for Spring Web MVC based on [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) (`ProblemDetail`).
It registers a `@RestControllerAdvice` ([`NrichProblemDetailExceptionHandler`][nrich-problem-detail-exception-handler-url]) that extends Spring's `ResponseEntityExceptionHandler`, so framework exceptions
(`MethodArgumentNotValidException`, `HttpRequestMethodNotSupportedException`, `ErrorResponseException`, ...) keep their native handling, while every response is enriched with a consistent set of nrich
extensions and a localized title and detail. Responses are returned as `application/problem+json`.

On top of the standard `ProblemDetail` fields the module adds these extension properties:

| Extension   | Description                                                                                       |
|-------------|---------------------------------------------------------------------------------------------------|
| `code`      | machine-readable error code the client can branch on                                              |
| `severity`  | `ERROR`, `WARNING` or `INFO` (see below)                                                           |
| `errorId`   | UUID correlating the response with the server-side exception log                                  |
| `timestamp` | ISO-8601 instant of when the error occurred                                                       |
| `errors`    | array of structured validation errors, present only for validation failures                      |

`severity` is resolved from the response status by default:

| Severity  | Default for                  |
|:---------:|------------------------------|
|  `ERROR`  | 5xx responses                |
| `WARNING` | 4xx responses                |
|  `INFO`   | any other response           |

Titles, details, codes, severities and statuses are all resolved from Spring's `MessageSource`, so they can be localized and customized per exception class (see [Customizing the response](#customizing-the-response)).

This module supersedes the notification-based error handling of [`nrich-notification`][nrich-notification-url] / [`nrich-webmvc`][nrich-webmvc-url]; it is a separate, Spring-native module rather than an
evolution of the old wire shape.

## ProblemDetail structure

A typical response body has the following shape (standard RFC 9457 fields plus the nrich extensions):

```json
{
    "type": "about:blank",
    "title": "Error",
    "status": 500,
    "detail": "Error occurred.",
    "instance": "/orders",
    "code": "order.processing-failed",
    "severity": "ERROR",
    "errorId": "8c3df784-ffa4-49fb-bb72-d08376be5186",
    "timestamp": "2026-06-01T10:15:30.245Z"
}
```

`type` stays `about:blank` (RFC 9457 §3.1) unless a `problemDetail.type.<fqcn>` message key is configured — this is resolved uniformly for every exception, using the same key Spring's `ErrorResponse` mechanism uses for its own framework exceptions. `instance` is set to the request path by Spring's body processors.

## Setting up Spring beans

When using the [`nrich-problem-detail-spring-boot-starter`][nrich-problem-detail-spring-boot-starter-url] all of the beans below are registered automatically (and can be overridden). For a plain Spring
(non-Boot) setup the following configuration is required:

```java

@Configuration
public class NrichProblemDetailConfiguration {

    @Bean
    public ValidationErrorResolvingService validationErrorResolvingService(MessageSource messageSource) {
        return new DefaultValidationErrorResolvingService(messageSource, false);
    }

    @Bean
    public ValidationErrorsProblemDetailContributor validationErrorsProblemDetailContributor(ValidationErrorResolvingService validationErrorResolvingService) {
        return new ValidationErrorsProblemDetailContributor(validationErrorResolvingService);
    }

    @Bean
    public CodeProblemDetailContributor codeProblemDetailContributor(MessageSource messageSource) {
        return new CodeProblemDetailContributor(messageSource, false);
    }

    @Bean
    public SeverityProblemDetailContributor severityProblemDetailContributor(MessageSource messageSource) {
        return new SeverityProblemDetailContributor(messageSource);
    }

    @Bean
    public ErrorIdProblemDetailContributor errorIdProblemDetailContributor() {
        return new ErrorIdProblemDetailContributor();
    }

    @Bean
    public TimestampProblemDetailContributor timestampProblemDetailContributor() {
        return new TimestampProblemDetailContributor();
    }

    @Bean
    public NrichProblemDetailExceptionHandler nrichProblemDetailExceptionHandler(MessageSource messageSource, LoggingService loggingService, List<ProblemDetailContributor> contributors) {
        return new NrichProblemDetailExceptionHandler(messageSource, loggingService, contributors, List.of(ExecutionException.class.getName()));
    }

}
```

The [`LoggingService`](../nrich-logging-api/src/main/java/net/croz/nrich/logging/api/service/LoggingService.java) bean comes from the [`nrich-logging`][nrich-logging-url] module and is used to log every
handled exception once, with the wire `errorId` as the correlation id.

---

### NrichProblemDetailExceptionHandler

[`NrichProblemDetailExceptionHandler`][nrich-problem-detail-exception-handler-url] is the `@RestControllerAdvice` at the center of the module. All exceptions funnel through a single
`handleExceptionInternal` override that, uniformly for every exception:

- resolves the HTTP status (`problemDetail.nrich.status.<fqcn>` &rarr; `@ResponseStatus` &rarr; `400` for validation exceptions &rarr; the handler default),
- resolves and sets the localized `title`, `detail` and `type`,
- logs the exception via [`LoggingService`][nrich-logging-url] with a generated `errorId`,
- runs every registered [`ProblemDetailContributor`][problem-detail-contributor-url] to enrich the body.

Wrapper exceptions listed in `exception-to-unwrap-list` (default `java.util.concurrent.ExecutionException`) are unwrapped to their cause and re-routed through the matching handler before the generic path.

### Contributors

The nrich extensions are added by a pipeline of [`ProblemDetailContributor`][problem-detail-contributor-url] beans. Each receives the [`ProblemDetailContributorContext`][problem-detail-contributor-context-url]
(exception, request, resolved status, locale, correlation id, timestamp) and mutates the `ProblemDetail` body. They run in `Ordered` sequence and each can be disabled independently (see the starter
README). The default contributors are:

| Contributor                              | Adds        | Resolved from                                                                          |
|------------------------------------------|-------------|----------------------------------------------------------------------------------------|
| `ValidationErrorsProblemDetailContributor` | `errors`    | delegates to [`ValidationErrorResolvingService`][validation-error-resolving-service-url] |
| `CodeProblemDetailContributor`           | `code`      | `problemDetail.nrich.code.<fqcn>`, then `ExceptionWithMessageCode`                       |
| `SeverityProblemDetailContributor`       | `severity`  | `problemDetail.nrich.severity.<fqcn>`, then the status-derived default                  |
| `ErrorIdProblemDetailContributor`        | `errorId`   | the context correlation id (same value that is logged)                                 |
| `TimestampProblemDetailContributor`      | `timestamp` | the context timestamp, formatted as an ISO-8601 instant                                |

Contributors mutate the body only — the HTTP status is resolved in the funnel before they run. To add your own extension, register an extra `ProblemDetailContributor` bean (optionally implementing
`Ordered`); to change how `errors[]` is built, override the [`ValidationErrorResolvingService`][validation-error-resolving-service-url] bean.

### ValidationErrorResolvingService

[`ValidationErrorResolvingService`][validation-error-resolving-service-url] turns validation failures into structured [`ValidationError`][validation-error-url] entries
(`field`, `rejectedValue`, `code`, `message`). The default implementation [`DefaultValidationErrorResolvingService`][default-validation-error-resolving-service-url] handles all three Spring validation paths —
`MethodArgumentNotValidException` (`@Valid @RequestBody`), `ConstraintViolationException` (service-layer `@Validated`) and `HandlerMethodValidationException` (constraints on `@RequestParam` /
`@PathVariable`). It keys message resolution on the **target class** rather than the binding object name, so per-DTO message customization is stable across parameter renames. By default `rejectedValue` is
omitted (so submitted values such as passwords or PII are not echoed back); set `include-rejected-value` to `true` to include it.

### Default MessageSource setup

`nrich-problem-detail` ships a [`nrich-problem-detail-messages`][nrich-problem-detail-messages-url] property file (with a Croatian `_hr` variant) containing the default titles and details. It is registered
into the application `MessageSource` automatically by the starter:

```properties
problemDetail.nrich.default-title=Error
problemDetail.nrich.validation-title=Validation failed
problemDetail.nrich.default-detail=Error occurred.
problemDetail.nrich.validation-detail=Validation failed.
```

## Usage

The following examples show the response bodies the handler produces. They assume the module is on the classpath (via the starter) and require no controller-side code beyond throwing the exception.

### Generic exceptions

A plain exception with no message keys, no `@ResponseStatus` and no `ExceptionWith*` markers produces a `500` with the default title and detail:

```json
{
    "type": "about:blank",
    "title": "Error",
    "status": 500,
    "detail": "Error occurred.",
    "instance": "/orders",
    "severity": "ERROR",
    "errorId": "8c3df784-ffa4-49fb-bb72-d08376be5186",
    "timestamp": "2026-06-01T10:15:30.245Z"
}
```

No `code` is emitted because nothing resolves one (no message key, not an `ExceptionWithMessageCode`).

### Customized exceptions

Given an exception that carries a message code and arguments:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends RuntimeException implements ExceptionWithMessageCode, ExceptionWithArguments {

    private final String orderId;

    @Override
    public String getMessageCode() {
        return "order.not-found";
    }

    @Override
    public Object[] getArgumentList() {
        return new Object[] { orderId };
    }
}
```

and these entries in `messages.properties`:

```properties
order.not-found=Order {0} could not be located
problemDetail.title.com.example.OrderNotFoundException=Order not found
problemDetail.type.com.example.OrderNotFoundException=https://errors.example.com/order-not-found
```

the response is:

```json
{
    "type": "https://errors.example.com/order-not-found",
    "title": "Order not found",
    "status": 404,
    "detail": "Order ORD-001 could not be located",
    "instance": "/orders/ORD-001",
    "code": "order.not-found",
    "severity": "WARNING",
    "errorId": "28e2b8f3-3ddf-4f70-89e2-29210b9ba644",
    "timestamp": "2026-06-01T10:15:30.245Z"
}
```

- `status` comes from `@ResponseStatus`, `severity` from the 4xx default,
- `detail` is resolved from the `order.not-found` key with `{0}` filled from `getArgumentList()`,
- `code` falls back to the exception's message code (no `problemDetail.nrich.code.<fqcn>` override was configured),
- `title` and `type` are the configured `problemDetail.title.<fqcn>` / `problemDetail.type.<fqcn>` values.

### Validation failures

An invalid `@Valid @RequestBody` (`MethodArgumentNotValidException`), service-layer `@Validated` (`ConstraintViolationException`) or method-parameter constraint
(`HandlerMethodValidationException`) all produce a `400` with the `errors[]` extension:

```json
{
    "type": "about:blank",
    "title": "Validation failed",
    "status": 400,
    "detail": "Validation failed.",
    "instance": "/orders",
    "errors": [
        { "field": "name", "rejectedValue": null, "code": "NotNull", "message": "must not be null" }
    ],
    "severity": "WARNING",
    "errorId": "bdadf9e4-d0f2-44e1-a882-661a683cb163",
    "timestamp": "2026-06-01T10:15:30.245Z"
}
```

## Customizing the response

All customization is done through message keys in the application `MessageSource` (`messages.properties`). Every key targets an exception by its fully qualified class name (`<fqcn>`):

| Key                                         | Customizes                                                                 |
|---------------------------------------------|----------------------------------------------------------------------------|
| `problemDetail.title.<fqcn>`                | `title`                                                                    |
| `problemDetail.<fqcn>`                      | `detail`                                                                   |
| `problemDetail.type.<fqcn>`                 | `type` (a problem-type URI; resolved for every exception, matching Spring's own convention) |
| `problemDetail.nrich.code.<fqcn>`           | `code`                                                                     |
| `problemDetail.nrich.severity.<fqcn>`       | `severity` (`ERROR` / `WARNING` / `INFO`)                                  |
| `problemDetail.nrich.status.<fqcn>`         | HTTP status (numeric, e.g. `422`)                                          |
| `problemDetail.nrich.logging.level.<fqcn>`  | log level for the exception (consumed by [`nrich-logging`][nrich-logging-url]) |
| `problemDetail.nrich.logging.verbosity.<fqcn>` | log verbosity for the exception                                         |

When no per-class title/detail key is found, the module falls back to `problemDetail.nrich.default-title` / `problemDetail.nrich.default-detail` (or the `validation-*` variants for validation exceptions).

### Customizing the detail through the exception

Instead of (or in addition to) message keys, an exception can carry its own detail by implementing the [`nrich-core-api`](../nrich-core-api/README.md) markers:

- [`ExceptionWithMessageCode`][exception-with-message-code-url] — `detail` and the fallback `code` are resolved from the returned message code,
- [`ExceptionWithMessage`][exception-with-message-url] — `detail` falls back to the exception message when no message key resolves,
- [`ExceptionWithArguments`][exception-with-arguments-url] — supplies the `{0}`, `{1}`, ... arguments used when resolving the detail message.

### Customizing validation messages

Validation `errors[].message` values are resolved per field. For a field error the resolution order is (most specific first):

- `<Constraint>.<TargetClassFqcn>.<field>` (e.g. `NotNull.com.example.CreateOrderRequest.name`)
- Spring's native field-error codes (`<Constraint>.<object>.<field>`, `<Constraint>.<field>`, `<Constraint>.<type>`, `<Constraint>`)
- the Bean Validation default message

So adding the following customizes the message for one field while leaving everything else on the framework defaults:

```properties
NotNull.com.example.CreateOrderRequest.name=Order name is required
```

[//]: # (Reference links)

[nrich-problem-detail-spring-boot-starter-url]: ../nrich-problem-detail-spring-boot-starter/README.md

[nrich-logging-url]: ../nrich-logging/README.md

[nrich-notification-url]: ../nrich-notification/README.md

[nrich-webmvc-url]: ../nrich-webmvc/README.md

[problem-detail-contributor-url]: ../nrich-problem-detail-api/src/main/java/net/croz/nrich/problemdetail/api/contributor/ProblemDetailContributor.java

[problem-detail-contributor-context-url]: ../nrich-problem-detail-api/src/main/java/net/croz/nrich/problemdetail/api/contributor/ProblemDetailContributorContext.java

[validation-error-resolving-service-url]: ../nrich-problem-detail-api/src/main/java/net/croz/nrich/problemdetail/api/service/ValidationErrorResolvingService.java

[validation-error-url]: ../nrich-problem-detail-api/src/main/java/net/croz/nrich/problemdetail/api/model/ValidationError.java

[nrich-problem-detail-exception-handler-url]: ../nrich-problem-detail/src/main/java/net/croz/nrich/problemdetail/handler/NrichProblemDetailExceptionHandler.java

[default-validation-error-resolving-service-url]: ../nrich-problem-detail/src/main/java/net/croz/nrich/problemdetail/service/DefaultValidationErrorResolvingService.java

[nrich-problem-detail-messages-url]: ../nrich-problem-detail/src/main/resources/nrich-problem-detail-messages.properties

[exception-with-message-code-url]: ../nrich-core-api/src/main/java/net/croz/nrich/core/api/exception/ExceptionWithMessageCode.java

[exception-with-message-url]: ../nrich-core-api/src/main/java/net/croz/nrich/core/api/exception/ExceptionWithMessage.java

[exception-with-arguments-url]: ../nrich-core-api/src/main/java/net/croz/nrich/core/api/exception/ExceptionWithArguments.java
