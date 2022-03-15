# Project structure

- Status: accepted
- Decision makers: agrancaric, jzrilic, mfolnovic, pzrinscak
- Date: 2020-05-11

## Context and Problem Statement

We need a consistent way of structuring code in the project. How should we structure our code?

## Decision Outcome

Our project should consist of multiple modules where each of the modules will have only one clearly defined purpose. Our modules will consist of three separate modules (where applicable, since not all
the modules require such a separation), the `api` module will contain public interfaces, classes for method parameters and results of those interfaces, `core` module containing the implementation that
depends on the api module and `spring-boot` module that contains Spring Boot's autoconfiguration for specified module and has dependencies on both `api` and `core` modules. Services in `core` module
implementing the interface from the `api` module should have a `Default` prefix. `spring-boot` module sole responsibility should only be providing the configuration for `core` module (the
configuration should consist of a `@Configuration` class and a `@ConfigurationProperties` class (if required) that will provide defaults for property value) while allowing for overriding of defined
beans through usage of `@ConditionalOnMissingBean`, `@CondtionalOnProperty` etc. The `spring-boot` project module will keep only the required dependencies in compile scope; other optional dependencies
should use special scopes defined in the main `build.gradle` configuration file.

Inter module dependencies should be avoided. Module dependencies should follow rules:

- `api` module dependencies should be kept at a minimum (meaning even in some cases not using Lombok unless there are multiple classes that require it)
- `core` module should not depend on any Spring Boot dependency
- `spring-boot` module should have `api` and `core` module dependencies as well as Spring Boot's autoconfiguration

The clients should only have the `api` module in Gradle's `api` scope (unless some overriding of service functionality is needed).

### Positive Consequences

- Clear, defined way of adding new modules
- Separation of concerns inside the module
- Minimal dependencies across modules
- Minimal dependencies inside module
- Easy way for clients to use the modules in both Spring Boot and only Spring environment

### Negative Consequences

- Might take some time getting used to new structure
- Not always clear what should go to `api` module
