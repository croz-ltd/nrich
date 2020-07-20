# nrich-security-csrf

## Overview

nrich-security-csrf is a library intended for CSRF protection. It generates a initial token from parameter initial token url and later expects token to be present in each request either
 as an HTTP parameter or HTTP header, if token is present it validates and refreshes token (token not present or invalid token generate a `CsrfTokenException`). It can function with Spring Web MVC and Spring WebFlux environments. 

## Setting up Spring beans


```

    @Bean
    public CsrfTokenManagerService tokenManagerService() {
        return new AesCsrfTokenManagerService(Duration.ofMinutes(35), Duration.ofMinutes(1), "X-CSRF-Token", 128);
    }

    @Bean
    public CsrfPingController csrfPingController() {
        return new CsrfPingController();
    }

    // when in servlet environment (Spring Web MVC)
    @Bean
    public CsrfInterceptor csrfInterceptor(final CsrfTokenManagerService csrfTokenManagerService) {
        final CsrfExcludeConfig csrfExcludeConfig = new CsrfExcludeConfig();

        csrfExcludeConfig.setUri("app/app");

        return new CsrfInterceptor(csrfTokenManagerService, "app/app", "/nrich/csrf/ping", Collections.singletonList(csrfExcludeConfig));
    }

    // when in reactive environment (Spring WebFlux)
    @Bean
    public CsrfWebFilter webFilter(final CsrfTokenManagerService csrfTokenManagerService) {
        final CsrfExcludeConfig csrfExcludeConfig = new CsrfExcludeConfig();

        csrfExcludeConfig.setUri("app/app");

        return new CsrfWebFilter(csrfTokenManagerService, "app/app", "/nrich/csrf/ping", Collections.singletonList(csrfExcludeConfig));
    }

    @Bean
    public WebMvcConfigurer csrfInterceptorWebMvcConfigurer(final CsrfInterceptor csrfInterceptor) {
        return new WebMvcConfigurer() {

            @Override
            public void addInterceptors(final InterceptorRegistry registry) {
                registry.addInterceptor(csrfInterceptor);
            }

        };
    }


``` 

`CsrfTokenManagerService` is a service that is responsible for generating initial token and validating and refreshing existing tokens. Default implementation `AesCsrfTokenManagerService`
uses `AES` algorithm for encryption with key length passed in as argument (in the former configuration it is 128). It also accepts token expiration interval, token future threshold (allows tokes to be in 
the future because of unsynchronized time between the client and server) and token name.

`CsrfPingController` is a controller that will be used as a ping call for Csrf token.   

`CsrfInterceptor` is used when in Spring Web MVC environment it is an implementation of `HandlerInterceptorAdapter` and it intercepts every request verifying token for every request except
excluded list and adding generated token for initial token url. Besides `CsrfTokenManagerService` it accepts initial token url, ping url and a list of `CsrfExcludeConfig` that contains either
 urls or regex that matches urls that will be skipped for CSRF token verification.

`CsrfWebFilter` is used when in Spring WebFlux environment it is an implementation of `WebFilter` it has the same behaviour and arguments as `CsrfInterceptor` with a difference it operates in reactive environment.


## Usage

Clients should configure initial token url. This is entry point in application i.e. controller action that is executed on a successful login. After that response from initial token action will
contain CSRF token in the parameter named `csrfInitialToken`. Every call from client to url that is not in the list of excluded urls should contain CSRF token or the call will fail.
Tokens will periodically be refreshed so users should refresh token value sent from client from received response header `X-CSRF-Token` (or other name that was specified in the configuration).
