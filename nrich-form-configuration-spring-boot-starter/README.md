# nrich-form-configuration-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-form-configuration-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-form-configuration-spring-boot-starter)

## Overview

This module is a Spring Boot starter for the [`nrich-form-configuration`][nrich-form-configuration-url] module. The purpose of [`nrich-form-configuration`][nrich-form-configuration-url] is to provide
a central place for constraint definitions by converting constraints defined on the server-side in a form that can be used on the client-side, thus avoiding constraint repetition on both server and
client side.

Starter module provides a `@Configuration` class ([`NrichFormConfigurationAutoConfiguration`][nrich-form-configuration-auto-configuration-url]) with default configuration of
[`nrich-form-configuration`][nrich-form-configuration-url] module, a `@ConfigurationProperties` class ([`NrichFormConfigurationProperties`][nrich-form-configuration-properties-url]) with default
configuration values and does automatic bean registration with `spring.factories`. The configuration class permits overriding with the help of conditional annotations.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository](https://search.maven.org/). To include the dependency use the following configurations.

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

Note if using [`nrich-bom`][nrich-bom-url] dependency versions should be omitted.

### Configuration

The configuration is done through a property file. Available properties and descriptions are given bellow (all properties are prefixed with **nrich.form-configuration** which is omitted for
readability):

| property                                            | description                                                                                                                                                                                                                                                                                                                                                   | default value |
|-----------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| default-converter-enabled                           | whether default converter service [`DefaultConstrainedPropertyValidatorConverterService`][default-constrained-property-validator-converter-service-url] for converting [`ConstrainedProperty`][constrained-property-url] instances to [`ConstrainedPropertyClientValidatorConfiguration`][constrained-property-client-validator-configuration-url] is enabled | true          |
| form-configuration-mapping                          | mapping between a client side form identifier and class holding the constraints for the form (usually the class accepted as input on the server side)                                                                                                                                                                                                         |               |
| form-validation-configuration-classes-package-list  | optional packages to scan for [`@FormValidationConfiguration`][form-configuration-annotation-url] annotated classes it can be used instead of form-configuration-mapping                                                                                                                                                                                      |               |

The default configuration values are given bellow in a yaml format for easier modification:

```yaml
nrich.form-configuration:
  default-converter-enabled: true
  form-configuration-mapping:
  form-validation-configuration-classes-package-list:
```

The default converter should be enabled unless users want to provide a completely custom implementation. To override the resolving of specific constraint properties display configuration a custom
implementation of [`ConstrainedPropertyValidatorConverterService`][constrained-property-validator-converter-service-url] interface should be provided. That interface implementation will be picked up
by the module and used before the default converter if `supports` method returns true.

### Using the module

Users should specify a `form-configuration-mapping` property map that will contain mappings between the form identifiers on the client-side and the classes that hold constraints on the server-side.

An example configuration is given bellow:

```yaml
nrich.form-configuration:
  default-converter-enabled: true
  form-configuration-mapping:
    user.create-form: net.croz.nrich.example.CreateUserRequest
    user.update-form: net.croz.nrich.example.UpdateUserRequest
```

As an alternative users can annotate `CreateUserRequest` and `UpdateUserRequest` classes with [`@FormValidationConfiguration`][form-configuration-annotation-url] annotation and use the following configuration:

```yaml
nrich.form-configuration:
  default-converter-enabled: true
  form-validation-configuration-classes-package-list: net.croz
```


This module exposes REST endpoint on `nrich/form/configuration/fetch` URL so that the client can retrieve constraint definitions. The endpoint expects a POST request with a list of form ids
(keys inside `form-configuration-mapping` map) for which a list with [`FormConfiguration`][form-configuration-url] will be returned for each mapped form id.
[`FormConfiguration`][form-configuration-url] contains a list of properties for constraints defined in server-side class which clients can then convert to specific client-side configurations and
apply them to their forms. If user needs to fetch constraint descriptions for all registered forms, module also exposes `nrich/form/configuration/fetch-all` endpoint that returns all configurations.

[//]: # (Reference links for readability)

[nrich-form-configuration-url]: ../nrich-form-configuration/README.md

[nrich-bom-url]: ../nrich-bom/README.md

[nrich-form-configuration-auto-configuration-url]: ../nrich-form-configuration-spring-boot-starter/src/main/java/net/croz/nrich/formconfiguration/starter/configuration/NrichFormConfigurationAutoConfiguration.java

[nrich-form-configuration-properties-url]: ../nrich-form-configuration-spring-boot-starter/src/main/java/net/croz/nrich/formconfiguration/starter/properties/NrichFormConfigurationProperties.java

[default-constrained-property-validator-converter-service-url]: ../nrich-form-configuration/src/main/java/net/croz/nrich/formconfiguration/service/DefaultConstrainedPropertyValidatorConverterService.java

[constrained-property-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/model/ConstrainedProperty.java

[constrained-property-client-validator-configuration-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/model/ConstrainedPropertyClientValidatorConfiguration.java

[constrained-property-validator-converter-service-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/service/ConstrainedPropertyValidatorConverterService.java

[form-configuration-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/model/FormConfiguration.java

[form-configuration-annotation-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/annotation/FormValidationConfiguration.java
