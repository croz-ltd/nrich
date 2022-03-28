# nrich-spring

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-spring/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-spring)

## Overview

nrich-spring is a library containing add-ons on Spring framework. Currently, no additional configuration is needed, just adding the library as dependency is enough. This library contains
only `ApplicationContextHolder` util class that provides access to `ApplicationContext` in static context. objects.

## Usage

If `ApplicationContext` is required in static context:

```
    ApplicationContextHolder.getApplicationContext()

```