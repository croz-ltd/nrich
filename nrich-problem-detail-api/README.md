# nrich-problem-detail-api

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-problem-detail-api/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-problem-detail-api)

## Overview

This module contains classes that represent the API (interfaces and data classes) for [`nrich-problem-detail`](../nrich-problem-detail/README.md) module.
It defines the contributor SPI (`ProblemDetailContributor`, `ProblemDetailContributorContext`) used to enrich the `ProblemDetail` body and the validation error resolving contract
(`ValidationErrorResolvingService`, `ValidationError`).
Default implementations are provided in [`nrich-problem-detail`](../nrich-problem-detail/README.md) module, but users can provide their own implementations.
If no overriding is required, only classes from this module should be used.
