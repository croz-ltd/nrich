# RFC 9457 ProblemDetail error handling as a new opt-in module

- Status: accepted
- Date: 2026-05-28

## Context and Problem Statement

`nrich-webmvc` ships `NotificationErrorHandlingRestControllerAdvice`, a `@RestControllerAdvice` that wraps every exception into a nrich `NotificationResponse` envelope. The shape predates Spring's first-class `ProblemDetail` (RFC 9457) support introduced in Spring 6 and Spring Boot 3. The two concerns no longer fit cleanly in one advice — the envelope, the message-code conventions (`<fqcn>.title`, `<fqcn>.message`), and the validation walk are all coupled, and downstream consumers are increasingly Spring-native and expect `application/problem+json` bodies with `problemDetail.*` message-code resolution.

How should nrich offer Spring-native ProblemDetail error handling without (a) breaking existing `NotificationErrorHandlingRestControllerAdvice` consumers, (b) duplicating Spring's `ErrorResponse` mechanism, or (c) reintroducing the per-feature nrich extensions (`errorId`, `severity`, `code`, structured `errors[]`, logging) by hand for every adopter?

## Decision Drivers

- Spring 6 / Boot 3 ship `ProblemDetail`, `ErrorResponseException`, and a `problemDetail.*` MessageSource resolution convention. Adopting them rather than competing with them.
- Existing nrich users on the notification advice should not have to migrate as part of a Spring Boot upgrade.
- The nrich-specific value-adds (`code`, `severity`, `errorId`, `timestamp`, structured `errors[]`, per-exception logging level/verbosity) are still desired but each should be individually disableable.
- The advice surface must be small enough to subclass / override cleanly; no leaking Spring internals into a bespoke nrich SPI.
- `-api` modules in nrich keep dependencies tight (see ADR-0004); a new module must respect that direction.

## Considered Options

- **A. Evolve `NotificationErrorHandlingRestControllerAdvice` in place** to emit a hybrid body carrying both the Notification envelope and ProblemDetail fields, gated by a property.
- **B. New, separate, opt-in module `nrich-problem-detail`** that subclasses `ResponseEntityExceptionHandler` and ships its own contributor pipeline. Existing notification advice stays untouched.
- **C. Pure message-replacement on the notification advice path**: keep the Notification envelope, only swap message-code conventions to `problemDetail.*`. No `ProblemDetail` body.

## Decision Outcome

Chosen option: **B — new opt-in module `nrich-problem-detail` (+ `-api`, `-spring-boot-starter`)**, because it is the only option that lets us adopt Spring's native ProblemDetail mechanics fully (`problemDetail.*` resolution, `ErrorResponseException`, `createProblemDetail`) without paying a back-compat tax on every line of the advice. Existing `NotificationErrorHandlingRestControllerAdvice` consumers keep working on the existing flag and are untouched by the new module landing.

The module subclasses Spring's `ResponseEntityExceptionHandler` and adds three things:

1. A single funnel — `handleExceptionInternal` — that every super-handled exception (`MethodArgumentNotValidException`, `ErrorResponseException`, …) and our two own handlers (`ConstraintViolationException`, generic `Exception`) route through. Super is called first so Spring's lazy `errorResponse.updateAndGetBody(messageSource, locale)` builds the body for typed handlers (preserving detail args + pre-built `ErrorResponseException` bodies); our funnel then resolves status uniformly and invokes the contributor pipeline. Uniformity is the firm value: every cross-cutting field works identically for every exception.
2. A `ProblemDetailContributor` SPI in the `-api` module with a `ProblemDetailContributorContext` carrying `exception`, `request`, resolved `status`, `locale`, a `correlationId` (generated once per exception, shared by `errorId` and logging contributors), and a `timestamp`. Default contributors — logging, validation errors, code, severity, errorId, timestamp — each ship as `@ConditionalOnMissingBean` + `@ConditionalOnProperty(matchIfMissing=true)` beans so adopters can individually disable any of them without affecting the others. Status resolution remains outside the contributor pipeline because it drives the `ResponseEntity` and must be computed before the body is built.
3. An `exceptionToUnwrapList` property (defaults to `java.util.concurrent.ExecutionException`, parity with the old `nrich.webmvc.exception-to-unwrap-list`) that lets the catch-all unwrap wrapper exceptions to their cause before routing — `ConstraintViolationException` re-dispatches to the validation handler; everything else flows through the generic 500 path with status / detail / code / log resolved on the unwrapped type.

A small `ValidationErrorResolvingService` (api interface, `Default*` implementation) that produces structured `errors[]` entries. The resolver prepends a class-stable nrich code (`<constraint>.<FQCN>.<field>`) to Spring's native `FieldError.getCodes()`, so per-DTO message customization is stable across binding-parameter renames; Spring's own object-name-based codes follow as the fallback ladder.

The wire output stays **flat** (`code`, `severity`, `errorId`, `timestamp`, `errors[]` as top-level extension properties on the ProblemDetail body, alongside the RFC-standard `type/title/status/detail/instance`). The message-code keys for nrich extensions live under a **`problemDetail.nrich.*`** namespace (`problemDetail.nrich.code.<fqcn>`, `problemDetail.nrich.status.<fqcn>`, `problemDetail.nrich.severity.<fqcn>`, `problemDetail.nrich.logging.level.<fqcn>`, `problemDetail.nrich.logging.verbosity.<fqcn>`) so they do not collide with Spring's own `problemDetail.title.<fqcn>` / `problemDetail.<fqcn>` (detail) / `problemDetail.type.<fqcn>` keys in the same bundle.

The new module also depends on `nrich-logging-api` and walks both the new `problemDetail.nrich.logging.{level,verbosity}.<fqcn>` keys and the existing `<fqcn>.loggingLevel` / `<fqcn>.loggingVerbosityLevel` keys (new first, existing as fallback) inside `Slf4jLoggingService` — see §9.1 of the design note. This keeps the dual-walk in one place so every `LoggingService` caller benefits, not only the new advice.

### Positive Consequences

- Existing `NotificationErrorHandlingRestControllerAdvice` consumers are untouched. The new module is purely additive on the way in.
- Spring's native `problemDetail.*` MessageSource resolution, `createProblemDetail`, `updateAndGetBody`, and `ErrorResponseException` are reused instead of reimplemented. The advice itself is small (~150 lines).
- Each nrich extension is a separate bean, individually disable-able with one property line.
- Spring Boot's own `ProblemDetailsExceptionHandler` is conditioned on `@ConditionalOnMissingBean(ResponseEntityExceptionHandler.class)`; since our advice extends that class, Boot's variant simply does not register when ours is present. No mutual-exclusivity machinery needed.

### Negative Consequences

- A subsequent (separately sequenced) phase is needed to retire the notification-based error handling: slim `nrich-notification` down to the success-notification path and decide what to do with `nrich-webmvc` (it is a leaf module today, but carries three unrelated utilities — `ControllerEditorRegistrationAdvice`, `TransientPropertyResolverService`, `ConstrainedSessionLocaleResolver` — that need a worth-assessment and a target home before the module can be deleted).
- Adopters who customize via the legacy `<fqcn>.title` / `<fqcn>.message` / `<fqcn>.httpStatus` message-code keys do not benefit until they re-key their bundle to `problemDetail.*` / `problemDetail.nrich.*`. Back-compat on the keys is intentionally not provided in the new module.

## Pros and Cons of the Options

### A. Evolve the notification advice in place

Pros

- Single advice for all consumers; no module multiplication.
- Existing properties / flags keep their meanings.

Cons

- The Notification envelope and the ProblemDetail wire shape do not compose cleanly — any "hybrid" body either ships both fields (and the consumer has to pick one) or invents a third shape neither side asked for.
- Dual message-code walks (`<fqcn>.*` and `problemDetail.*`) leak into every resolution path, and the existing advice's coupling to `ConstraintConversionService` / `MessageCodeConfig` makes the change invasive.
- Spring's `ErrorResponseException` and `createProblemDetail` cannot be cleanly reused without already committing to ProblemDetail as the body shape.

### B. New opt-in `nrich-problem-detail` module (chosen)

Pros

- Native Spring shape end-to-end. We inherit Spring's mechanism rather than competing with it.
- Existing advice stays byte-for-byte unchanged on its flag. Zero migration cliff.
- Each nrich extension is a bean with a property — easy to disable, override, or replace via the SPI.
- The `problemDetail.nrich.*` namespacing keeps nrich extensions visually grouped in bundles without colliding with Spring's standard keys.

Cons

- A new module adds surface to maintain — but the surface is small (4 api types, 12 core classes, 2 starter classes) and the implementation leans heavily on Spring's existing machinery.
- The eventual removal of the notification advice and webmvc retirement is a follow-up effort.

### C. Pure message-replacement on the notification path

Pros

- No new module; no change to existing wire shape.

Cons

- Loses the structural goal of moving to ProblemDetail. Adopters still get the Notification envelope; consumers asking for `application/problem+json` are still not served.
- We still inherit the dual-convention complexity in resolution; the visible improvement is small relative to the change cost.

## Links

- Refined design note: [`support/documentation/design-notes/problem-detail-new-module-plan.md`](../../design-notes/problem-detail-new-module-plan.md) — class inventory, contributor list, validation handling, configuration surface, test plan, and full §16 source skeletons.
- Extends ADR-0004 (project structure) — the new module follows the api / core / starter layering and dependency direction defined there.
