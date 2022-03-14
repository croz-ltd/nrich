# nrich-security-csrf

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-security-csrf/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-security-csrf)

## Overview

`nrich-security-csrf` is a library intended for CSRF protection. It generates a initial token from initial token url and later expects token to be present in each request either as an HTTP
parameter or HTTP header. If token is present it validates and refreshes token. If token is not present or the token is invalid, `CsrfTokenException` is thrown.
It works in both Spring Web MVC and Spring WebFlux environments.

## Setting up Spring beans

```java

@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {

    @Bean
    public CsrfTokenManagerService tokenManagerService() {
        return new AesCsrfTokenManagerService(Duration.ofMinutes(35), Duration.ofMinutes(1), 128);
    }

    @Bean
    public CsrfPingController csrfPingController() {
        return new CsrfPingController();
    }

    // when in servlet environment (Spring Web MVC)
    @Bean
    public CsrfInterceptor csrfInterceptor(CsrfTokenManagerService csrfTokenManagerService) {
        CsrfExcludeConfig csrfExcludeConfig = new CsrfExcludeConfig();

        csrfExcludeConfig.setUri("app/app");

        return new CsrfInterceptor(csrfTokenManagerService, "X-CSRF-Token", "app/app", "/nrich/csrf/ping", Collections.singletonList(csrfExcludeConfig));
    }

    @Bean
    public WebMvcConfigurer csrfInterceptorWebMvcConfigurer(CsrfInterceptor csrfInterceptor) {
        return new WebMvcConfigurer() {

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(csrfInterceptor);
            }

        };
    }

    // when in reactive environment (Spring WebFlux)
    @Bean
    public CsrfWebFilter webFilter(CsrfTokenManagerService csrfTokenManagerService) {
        CsrfExcludeConfig csrfExcludeConfig = new CsrfExcludeConfig();

        csrfExcludeConfig.setUri("app/app");

        return new CsrfWebFilter(csrfTokenManagerService, "X-CSRF-Token", "app/app", "/nrich/csrf/ping", Collections.singletonList(csrfExcludeConfig));
    }
}

```

`CsrfTokenManagerService` is a service responsible for generating initial token and validating and refreshing existing tokens. Default implementation `AesCsrfTokenManagerService`
uses `AES` algorithm for encryption with key length passed in as argument (in the former configuration it is 128). It also accepts token expiration interval, token future threshold (allows tokes to be
in the future because of unsynchronized time between the client and server).

`CsrfPingController` is a controller that will be used for a ping call for Csrf token.

`CsrfInterceptor` is used when in Spring Web MVC environment. It is an implementation of `HandlerInterceptorAdapter` which intercepts every request and verifies token for every request
except excluded list. It also adds generated token for initial token url. Besides `CsrfTokenManagerService` it accepts the token name, initial token url, ping url and a list of `CsrfExcludeConfig`
that contains either urls or regex that matches urls that will be skipped for CSRF token verification.

`CsrfWebFilter` is used when in Spring WebFlux environment. It is an implementation of `WebFilter` and has the same behaviour and arguments as `CsrfInterceptor` with a difference it operates in
reactive environment.

## Usage

Clients should configure initial token url. This is entry point in application i.e. controller action that is executed on a successful login. After that response from initial token action will contain
CSRF token in the parameter named `csrfInitialToken`. Every call from client to url that is not in the list of excluded urls should contain CSRF token or the call will fail. Tokens will periodically
be refreshed so users should refresh token value sent from client from received response header `X-CSRF-Token` (or other name that was specified in the configuration).
