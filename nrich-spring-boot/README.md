# nrich-spring-boot

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-spring-boot/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-spring-boot)

## Overview

nrich-spring-boot is a library containing add-ons on Spring Boot framework. Currently, no additional configuration is needed, just adding the library as dependency is enough. This library contains
only `ConditionalOnPropertyNotEmpty` annotation (for now). It is used to register beans only when property is not empty, it supports strings, list of strings and lists of objects.

## Usage

```

    @ConditionalOnPropertyNotEmpty("example.bean.condition.list")
    @Bean
    public ExampleBean exampleBean() {
        return new ExampleBean();
    }

```

For usage it is enough to add annotation to bean that should only be registered when property is not empty. In former example bean `ExampleBean`
will only be registered when property `example.bean.condition.list` is not empty.
