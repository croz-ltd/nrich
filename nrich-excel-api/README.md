# nrich-excel-api

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-excel-api/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-excel-api)

## Overview

Contains classes that represent the API (interfaces and data classes) for `nrich-excel` library. The interfaces have default implementations provided in
`nrich-excel` library but users can provide their own implementations. Classes present here also provide a way for customizing writing to excel as well as defining variable values for templates.
Unless some overriding is required only classes from this library should be used directly, other configuration should be provided through property files.
