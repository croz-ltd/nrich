# nrich-form-configuration-api

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-form-configuration-api/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-form-configuration-api)

## Overview

Contains classes that represent the API (interfaces, data classes and requests) for `nrich-form-configuration` module. The interfaces have default implementations provided
in `nrich-form-configuration` module but users can provide their own implementations. Unless some overriding is required only classes from this module should be used directly, other configuration
should be provided through property files.
