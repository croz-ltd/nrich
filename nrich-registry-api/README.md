# nrich-registry-api

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-registry-api/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-registry-api)

## Overview

Contains classes that represent the API (interfaces, configuration classes and data classes) for `nrich-registry` module. The interfaces have default implementations provided in
`nrich-registry` module but users can provide their own implementations. Unless some overriding is required only classes from this module should be used directly, other configuration should be
provided through property files.
