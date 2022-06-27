# nrich-core-api

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-core-api/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-core-api)

## Overview

Contains classes that are used across other nrich modules but can also be used independently of nrich.
Two classes are provided: a custom exception `EntityNotFoundException` that can be used inside projects to indicate that an entity was not found, and `ExceptionWithArguments` interface
that provides a way for adding arguments for exception message resolving from `MessageSource` inside `NotificationErrorHandlingRestControllerAdvice`.
