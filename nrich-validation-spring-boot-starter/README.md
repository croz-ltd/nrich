# nrich-validation-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-validation-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-validation-spring-boot-starter)

## Overview

Spring Boot starter for `nrich-validation` module. The purpose of `nrich-validation` module is converting constraints defined on the server side in a form that can be used on the client side thus
avoiding repeating constraints on both server and client side.
Starter module provides a `@Configuration` class (`NrichValidationAutoConfiguration`) with default configuration of `nrich-validation` module (while allowing for overriding with conditional
annotations) and does automatic registration through `spring.factories`.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository](https://search.maven.org/). To include the dependency use the following configurations.

With Maven:

```xml

<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-validation-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>

```

With Gradle:

```groovy

implementation "net.croz.nrich:nrich-validation-spring-boot-starter:${nrich.version}"

```

Note if using `nrich-bom` dependency versions should be omitted.

### Configuration

Configuration is done through a property file, available properties and descriptions are given bellow (all properties are prefixed with nrich.validation which is omitted for readability):

| property           | description                                                      | default value |
|--------------------|------------------------------------------------------------------|---------------|
| register-messages  | Whether default validation failure messages should be registered | true          |

The default configuration values in yaml format for easier modification are given bellow:

```yaml

nrich.validation:
  register-messages: true

```

### Using the module

Users should just add the dependency on classpath and then use the provided constraints. If custom messages are required they should be defined in `messages.properties` file
and default ones disabled then through `nrich.validation.register-messages` property set to false. A list of available constraints and descriptions is given bellow:

| constraint                | description                                                                                                                                                                                       |
|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `@InList`                 | Validates that the annotated element is in the specified list of values                                                                                                                           |
| `@MaxSizeInBytes`         | Validates that the annotated element size in bytes must be less than specified maximum                                                                                                            |
| `@NotNullWhen`            | Validates that the annotated element must not be null when condition is satisfied                                                                                                                 |
| `@NullWhen`               | Validates that the annotated element must be null when condition is satisfied                                                                                                                     |
| `@SpellExpression`        | Validates the annotated element with the defined SpEL expression                                                                                                                                  |
| `@ValidFile`              | Validates that the annotated element matches specified content type list, allowed extension list and/or allowed regex                                                                             |
| `@ValidFileResolvable`    | Validates that the annotated element matches specified content type list, allowed extension list and/or allowed regex                                                                             |
| `@ValidOib`               | Validates that the annotated element is valid OIB (Personal Identification number)                                                                                                                |
| `@ValidRange`             | Validates that the annotated element from property must be less than (or equal to if inclusive is true) to property                                                                               |
| `@ValidSearchProperties ` | Validates that at least one group of annotated element must contain all properties that are not null (i.e. when searching users that either name is not null or first and last name are not null) |


#### File related constraints

Difference between `@ValidFile` and `@ValidFileResolvable` is that the former resolves allowed values from environment. Searched properties are given bellow but can be overridden on each constraint
(all properties are prefixed with nrich.constraint.file which is omitted for readability):

| property                  | description                                                                                                          |
|---------------------------|----------------------------------------------------------------------------------------------------------------------|
| allowed-content-type-list | Property name from which allowed content type list is resolved (empty value allows all content types)                |
| allowed-extension-list    | Property name from which allowed extension list is resolved (case-insensitive, empty value allows all content types) |
| allowed-file-name-regex   | Property name from which allowed file name regex is resolved (empty value allows all file names)                     |
