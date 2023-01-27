[![Build](https://github.com/croz-ltd/nrich/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/croz-ltd/nrich/actions/workflows/build.yml)
[![Codecov](https://codecov.io/gh/croz-ltd/nrich/branch/master/graph/badge.svg)](https://codecov.io/gh/croz-ltd/nrich)
[![License](https://img.shields.io/github/license/croz-ltd/nrich?color=yellow&logo=apache)](https://github.com/croz-ltd/nrich/blob/master/LICENSE)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-core-api/badge.svg?color=blue)](https://search.maven.org/search?q=net.croz.nrich)
[![JavaDoc](https://img.shields.io/badge/API%20doc-Javadoc-brightgreen)](https://croz-ltd.github.io/nrich)

# nrich

Nrich is a Java library developed at CROZ whose purpose is to make development of applications on the JVM a little easier.
It was created by combining modules that were found useful on multiple projects into a common library.
The library is based on the Spring Framework, and as such provides Spring Boot starters for most of the modules to make the configuration easier.

Most functionality groups are composed of multiple modules:

- api - has the `api` suffix and contains classes that represent API of the module (service interfaces, parameters, return types...)
- implementation - contains actual api implementation
- spring boot starter - has the `spring-boot-starter` suffix and contains Spring Boot auto-configuration of the specified module

In the Spring Boot environment only spring boot starter modules should be added as dependencies.

## Compatibility

The following table contains nrich versions with a corresponding minimum Java and Spring version.

| Nrich version | Java version | Spring Boot Version |
|---------------|--------------|---------------------|
| 1.x.x         | 1.8          | 2.3.3.RELEASE       |
| 1.2.x         | 1.8          | 2.6.4               |
| 1.3.x         | 1.8          | 2.6.6               |
| 1.4.x         | 1.8          | 2.6.6               |
| 1.5.x - 1.7.x | 1.8          | 2.7.4               |
| 1.8.x         | 1.8          | 2.7.7               |

## Modules

Nrich is composed of following modules:

### [nrich-bom](nrich-bom/README.md)

Provides managed dependencies for all nrich modules as well as for libraries used inside those modules that are not covered by Spring Boot dependencies.

### [nrich-core-api](nrich-core-api/README.md)

This module contains common classes that are used throughout the library.

### [nrich-encrypt](nrich-encrypt/README.md)

The module achieves easier encryption and decryption by being able to encrypt method results and decrypt method arguments.
Methods whose results should be encrypted and/or arguments decrypted can be marked using annotations or as properties specified
in the property file (such as `application.yml`).

### [nrich-excel](nrich-excel/README.md)

Provides easier generation of excel reports from provided data and templates.
Default implementation uses `Apache POI`library but tries to simplify usage.

### [nrich-form-configuration](nrich-form-configuration/README.md)

Provides mapping of server-side class constraints to client-side form constraints.
The purpose of `nrich-form-configuration` is to provide a central place for constraint definitions.
The client registers a form to the class that defines constraints, which enables him to request information for the registered form.
Supplied information contains constraints with their error messages, which are specified in the class.
The client is then responsible for processing and applying them to the form.

### [nrich-jackson](nrich-jackson/README.md)

Sets commonly used defaults for standard Jackson properties (as an example `FAIL_ON_EMPTY_BEANS: false`) to avoid repetition in projects.
The module also provides Jackson modules that serialize empty strings to null and serialize class names to fully-qualified class names for
classes that are annotated with `@Entity` annotation or are from a package defined in a given list.

### [nrich-logging](nrich-logging/README.md)

Provides a logging service for consistent error logging in a standard format.
Provided logging service can also resolve verbosity and logging levels for each exception.
This module is used in [nrich-webmvc](nrich-webmvc/README.md) module for exception logging but can easily be replaced with some other custom implementation.

### [nrich-notification](nrich-notification/README.md)

Intended for addition of specified notifications into the server-side response, which can later be shown on the client-side.
`nrich-notification` supports three different notification severity levels and can also include a list of validation errors.
Definition and resolution of messages is realized with Spring's `MessageSource` feature.

### [nrich-registry](nrich-registry/README.md)

Simplifies the administration of specified JPA entities and provides a formatted representation of them that client-side can interpret and create dynamic forms and grids.
Generated forms and grids can be used for entity editing without additional implementation on the server-side.
The module also provides methods for searching, creating, updating and deleting entities through a REST API.

### [nrich-search](nrich-search/README.md)

Simplifies the querying of entities.
`nrich-search` is based on top of the Spring Data JPA library and was created as a means of simplifying the creation of queries for various search forms on the client-side.
Queries can be automatically formed from different types of inputs, such as a query class or a string with a list of search fields.
Also, special configuration class is used to define how provided query values are used in query creation.

### [nrich-security-csrf](nrich-security-csrf/README.md)

Intended as a replacement for Spring Security csrf functionality.
`nrich-security-csrf` works with both Spring Web MVC and WebFlux libraries.
Clients should define the initial token url and after that send the generated token with each request in a header or as a parameter.

### [nrich-spring-boot](nrich-spring-boot/README.md)

Adds additional functionality to Spring Boot that is used within nrich.

### [nrich-spring](nrich-spring/README.md)

Contains utility classes for Spring access, such as `ApplicationContextHolder` for resolving of `ApplicationContext` from static context.

### [nrich-validation](nrich-validation/README.md)

Contains additional `jakarta-validation-api` constraints and validators that proved to be commonly used, such as `NotNullWhen` validator.

### [nrich-webmvc](nrich-webmvc/README.md)

Provides additional functionality built on top of the Spring Web MVC framework.
Main purpose is to handle exceptions through `NotificationErrorHandlingRestControllerAdvice` that handles exceptions by logging them,
and then creating and sending notifications to the client-side with exception's description.
The module uses the [nrich-notification](nrich-notification/README.md) module for notification handling and [nrich-logging](nrich-logging/README.md) for logging.
`nrich-webmvc` also adds additional classes that handle binding (i.e. transforming empty string to null) and locale resolving.
