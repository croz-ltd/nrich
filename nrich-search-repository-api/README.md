# nrich-search-repository-api

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-search-repository-api/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-search-repository-api)

## Overview

Contains classes that represent the repository API (repository interfaces and factories) for `nrich-search` module. The interfaces have default implementations provided in `nrich-search` module but
users can provide their own implementations. Unless some overriding is required only classes from this module should be used directly, other configuration should be provided through property files.
