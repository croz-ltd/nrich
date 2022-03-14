# nrich-form-configuration-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-form-configuration-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-form-configuration-spring-boot-starter)

## Overview

Spring Boot starter for `nrich-form-configuration` module. The purpose of `nrich-form-configuration` is converting constraints defined on the server side in a form that can be used on the client side
to apply the same constraints on the client thus avoiding repeating constraints on both server and client side. Starter module provides a `@Configuration`
class (`NrichFormConfigurationAutoConfiguration`) with default configuration of `nrich-form-configuration` module (while allowing for overriding with conditional annotations)
and a `@ConfigurationProperties` class (`NrichFormConfigurationProperties`) with default configured values and does automatic registration through `spring.factories`.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository][Maven Central]. To include the dependency use the following configurations.

With Maven:

```xml

<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-form-configuration-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>

```

With Gradle:

```groovy

implementation "net.croz.nrich:nrich-form-configuration-spring-boot-starter:${nrich.version}"

```

Note if using `nrich-bom` dependency versions should be omitted.

### Configuration

Configuration is done through a property file, available properties and descriptions are given bellow (all properties are prefixed with nrich.form-configuration which is omitted for readability):

| property                   | description                                                                                                                                                                                            | default value |
|----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| default-converter-enabled  | Whether default converter service `DefaultConstrainedPropertyValidatorConverterService` for converting `ConstrainedProperty` instances to `ConstrainedPropertyClientValidatorConfiguration` is enabled | true          |
| form-configuration-mapping | Mapping between a client side form identifier and class holding the constraints for the form (usually the class accepted as input on the server side)                                                  |               |

The default configuration values in yaml format for easier modification are given bellow:

```yaml

nrich.form-configuration:
  default-converter-enabled: true
  form-configuration-mapping:

```

The default converter should be enabled unless users want to provide a completely custom implementation. Overriding of specific constraint properties display configuration resolving can be
done by implementing a `ConstrainedPropertyValidatorConverterService` interface. That interface implementation will be picked up by the library and used before default converter if `supports`
method returns true.

### Using the library

Users should specify a form-configuration-mapping property list that will contain mappings between client side form identifier and server side input. An example configuration is given bellow:

```yaml

nrich.form-configuration:
  default-converter-enabled: true
  form-configuration-mapping:
    user.create-form: net.croz.nrich.example.CreateUserRequest
    user.update-form: net.croz.nrich.example.UpdateUserRequest

```

This library should be used from the client side and for that a REST endpoint is exposed on `nrich/form/configuration/fetch` URL. The endpoint expects a list of formIdList
(key inside form-configuration-mapping map) for which the `FormConfiguration` list will be returned. `FormConfiguration` contains a list of properties with defined constraints, clients can then
convert that configuration to specific client configurations and apply them to their forms.
