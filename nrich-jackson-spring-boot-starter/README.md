# nrich-jackson-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-jackson-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-jackson-spring-boot-starter)

## Overview

Spring Boot starter for `nrich-jackson` module. The purpose of `nrich-jackson` is to provide additional Jackson's serializers and deserializers.
Starter module provides a `@Configuration` class  (`NrichJacksonAutoConfiguration`) with default configuration of `nrich-jackson` module
and a `@ConfigurationProperties` class (`NrichJacksonProperties`) with default configured values and does automatic registration through `spring.factories`. On top of that it configures
Spring Boot's `ObjectMapper` instance with some useful defaults defined in nrich-jackson.properties file (i.e. spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false)

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository](https://search.maven.org/). To include the dependency use the following configurations.

With Maven:

```xml

<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-jackson-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>

```

With Gradle:

```groovy

implementation "net.croz.nrich:nrich-jackson-spring-boot-starter:${nrich.version}"

```

Note if using `nrich-bom` dependency versions should be omitted.

### Configuration

Configuration is done through a property file, available properties and descriptions are given bellow (all properties are prefixed with nrich.jackson which is omitted for readability):

| property                                             | description                                                                              | default value |
|------------------------------------------------------|------------------------------------------------------------------------------------------|---------------|
| convert-empty-strings-to-null                        | Whether empty strings should be converted to null values                                 | true          |
| serialize-class-name                                 | Whether class name should be serialized                                                  | true          |
| serialize-class-name-for-entity-annotated-classes    | Whether class name should be serialized for classes annotated with JPA Entity annotation | true          |
| additional-package-list-for-class-name-serialization | Package list for which class name should also be serialized                              |               |

The default configuration values in yaml format for easier modification are given bellow:

```yaml

nrich.jackson:
  convert-empty-strings-to-null: true
  serialize-class-name: true
  serialize-class-name-for-entity-annotated-classes: true
  additional-package-list-for-class-name-serialization:

```

On top of that following properties are configured for Spring Boot's `ObjectMapper`:

```yaml

spring.jackson:
  default-property-inclusion: non_null
  mapper.PROPAGATE_TRANSIENT_MARKER: true
  parser.ALLOW_COMMENTS: true
  serialization:
    FAIL_ON_EMPTY_BEANS: false
    WRITE_DATES_AS_TIMESTAMPS: false
    WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS: false

  deserialization:
    ACCEPT_SINGLE_VALUE_AS_ARRAY: true
    FAIL_ON_UNKNOWN_PROPERTIES: false

```

### Using the module

This module configures `ObjectMapper` instance by adding additional modules and options to it. It is used when serializing or deserializing JSON using `ObjectMapper` bean.
For example if `nrich.jackson.convert-empty-strings-to-null` is active and received JSON contains empty strings they will be converted to null values when deserializing JSON to class instance.
