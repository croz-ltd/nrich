# nrich-encrypt-api

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-encrypt-api/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-encrypt-api)

## Overview

Contains classes that represent the API (interfaces, configuration classes and annotations) for `nrich-encrypt` module.
The interfaces have default implementations provided in `nrich-encrypt` module but users can provide their own implementations. Unless some overriding is required only classes from this module
should be used directly, other configuration should be provided through property files.
