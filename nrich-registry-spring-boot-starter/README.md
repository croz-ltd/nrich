# nrich-registry-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-registry-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-registry-spring-boot-starter)

## Overview

Spring Boot starter for `nrich-registry` module. The purpose of `nrich-registry` is to provide a representation of JPA entities in a format that can be used on the client side to build dynamic forms
and tables. It provides a REST API for querying, updating and deleting JPA entities as well as history data access (if Hibernate Envers is on classpath). Configuration of searchable properties
and conversion from string to property type is also supported. Starter module provides a `@Configuration` class (`NrichRegistryAutoConfiguration`)
with default configuration of `nrich-registry` module (while allowing for overriding with conditional annotations), and a `@ConfigurationProperties` class (`NrichRegistryProperties`)
with default configured values and does automatic registration through `spring.factories`.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository](https://search.maven.org/). To include the dependency use the following configurations.

With Maven:

```xml

<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-registry-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>

```

With Gradle:

```groovy

implementation "net.croz.nrich:nrich-registry-spring-boot-starter:${nrich.version}"

```

Note if using `nrich-bom` dependency versions should be omitted.

### Configuration

Configuration is done through a property file, available properties and descriptions are given bellow (all properties are prefixed with nrich.registry which is omitted for readability):

| property                                          | description                                                                                                                | default value                                             |
|---------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------|
| default-read-only-property-list                   | List of property names that should always be marked as readonly                                                            |                                                           |
| default-converter-enabled                         | Whether default string to type converter used for converting strings to property values when searching registry is enabled | true                                                      |
| default-java-to-javascript-converter-enabled      | Whether default Java to Javascript type converter is enabled                                                               | true                                                      |
| registry-search.date-format-list                  | List of date formats used to convert string to date value                                                                  | dd.MM.yyyy., dd.MM.yyyy.'T'HH:mm, dd.MM.yyyy.'T'HH:mm'Z'  |
| registry-search.decimal-number-format-list        | List of decimal formats used to convert string to decimal value                                                            | #0.00, #0,00                                              |
| registry-search.boolean-true-regex-pattern        | Regexp pattern that is used to match boolean true values                                                                   | ^(?i)\s*(true&#124;yes&#124;da)\s*$                       |
| registry-search.boolean-false-regex-pattern       | Regexp pattern that is used to match boolean false values                                                                  | ^(?i)\s*(false&#124;no&#124;ne)\s*$                       |

The properties under registry-search are used when converting string received from client to property values that will be used to search registry. For example if a string is sent
and the property searched by is of type Date, nrich will try to parse string to Date and if parsing succeeds restriction will be added to query and if parsing fails the property will be skipped
(no exception is thrown or logged).

The default configuration values in yaml format for easier modification are given bellow:

```yaml

nrich.registry:
  default-read-only-property-list:
  default-converter-enabled: true
  default-java-to-javascript-converter-enabled: true
  registry-search:
    date-format-list: dd.MM.yyyy., dd.MM.yyyy.'T'HH:mm, dd.MM.yyyy.'T'HH:mm'Z'
    decimal-number-format-list: #0.00, #0,00
    boolean-true-regex-pattern: ^(?i)\s*(true|yes|da)\s*$
    boolean-false-regex-pattern: ^(?i)\s*(false|no|ne)\s*$

```

Overriding of default registry behaviour (i.e. in search configuration which operators are used for searching) is possible by defining a bean of
type [RegistryOverrideConfigurationHolder](../nrich-registry-api/src/main/java/net/croz/nrich/registry/api/core/model/RegistryOverrideConfigurationHolder.java)

### Using the module

This module is meant to be used through REST API and as such exposes multiple endpoints. For a detailed description of each endpoint see `nrich-registry` [README.MD](../nrich-registry/README.md).
Bellow is just a short overview of available endpoints (all methods use HTTP POST method):

| request path                          | description                                                                                    |
|---------------------------------------|------------------------------------------------------------------------------------------------|
| `nrich/registry/configuration/fetch`  | Fetches configuration of all entities (used on client for generating dynamic forms and tables) |
| `nrich/registry/data/list-bulk`       | Lists multiple registry entities                                                               |
| `nrich/registry/data/list`            | List a single registry entity (also supports searching if query parameter is specified)        |
| `nrich/registry/data/create`          | Creates registry entity                                                                        |
| `nrich/registry/data/update`          | Update registry entity                                                                         |
| `nrich/registry/data/delete`          | Deletes registry entity                                                                        |
| `nrich/registry/history`              | List all the revisions of the entity (available only if envers is on classpath)                |
