# nrich-security-csrf-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-security-csrf-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-security-csrf-spring-boot-starter)

## Overview

Spring Boot starter for `nrich-security-csrf` module. The purpose of `nrich-security-csrf` is to provide an alternative to Spring Boot's CSRF implementation. It supports both reactive and servlet
environments. Starter module provides a `@Configuration` class (`NrichCsrfAutoConfiguration`) with default configuration of `nrich-security-csrf`
module (while allowing for overriding with conditional annotations), and a `@ConfigurationProperties` class (`NrichCsrfProperties`) with default configured values
and does automatic registration through `spring.factories`.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository][Maven Central]. To include the dependency use the following configurations.

With Maven:

```xml

<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-security-csrf-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>

```

With Gradle:

```groovy

implementation "net.croz.nrich:nrich-security-csrf-spring-boot-starter:${nrich.version}"

```

Note if using `nrich-bom` dependency versions should be omitted.

### Configuration

Configuration is done through a property file, available properties and descriptions are given bellow (all properties are prefixed with nrich.security.csrf which is omitted for readability):

| property                  | description                                                                                                                                              | default value      |
|---------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------|
| active                    | Whether CSRF is active                                                                                                                                   | true               |
| token-expiration-interval | Duration of CSRF token                                                                                                                                   | 35m                |
| token-future-threshold    | Duration of how long token can be in the future (can happen when server and client time is not in sync)                                                  | 1m                 |
| token-key-name            | Name of CSRF token                                                                                                                                       | X-CSRF-Token       |
| crypto-key-length         | Length of crypto key (128, 256...)                                                                                                                       | 128                |
| initial-token-url         | Initial application url (i.e. url that user is redirected to after login). Token will be added to response from this url as `csrfInitialToken` parameter |                    |
| csrf-ping-uri             | Uri used for CSRF ping request                                                                                                                           | `/nrich/csrf/ping` |
| csrf-exclude-config-list  | A list of instances that contain urls or regexps excluded from CSRF check                                                                                |                    |

The default configuration values in yaml format for easier modification are given bellow:

```yaml

nrich.security.csrf:
  active: true
  token-expiration-interval: 35m
  token-future-threshold: 1m
  token-key-name: X-CSRF-Token
  crypto-key-length: 128
  initial-token-url:
  csrf-ping-uri: /nrich/csrf/ping
  csrf-exclude-config-list:

```

### Using the library

The library supports both reactive and servlet environments and will register either a custom `HandlerInterceptor` if in a servlet environment or a custom `WebFilter`
if in a reactive environment. An example configuration from properties file is given bellow:

```yaml

nrich.security.csrf:
  initial-token-url: /main/init
  csrf-ping-uri: /nrich/csrf/ping
  csrf-exclude-config-list:
    - uri: /main/init
    - regex: .*css

```

The main/init uri is the first url that is accessed by the clients. When invoking this url a new CSRF token will be added as a parameter. Clients should then add the that token to each request when
making further requests to server side. Token is checked on every url not in `csrf-exclude-config-list`. The token can be added either as a request parameter or as a header (both should have the name
specified as in `token-key-name`).
