# nrich-jackson

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-jackson/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-jackson)

## Overview

nrich-jackson is a library containing that adds additional Jacksons serializers and deserializers that have proved to be useful in projects
(i.e. serializing empty strings to null, deserializing class property with certain classes etc.)

## Setting up Spring beans

Functionalities of this library are defined as separate Jackson modules. And are located in `JacksonModuleUtil` class. Modules can them self be declared as beans and will be found automatically when
Spring Boot is used or they can be manullay registered on
`ObjectMapper` instance.

```

    @Bean
    public ObjectMapper registryObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(JacksonModuleUtil.convertEmptyStringToNullModule());
        objectMapper.registerModule(JacksonModuleUtil.classNameSerializerModule(true, Collections.singletonList("net.croz.test")));

        return objectMapper;
    }

```

`JacksonModuleUtil.convertEmptyStringToNullModule` is module responsible for converting empty strings to null values
`JacksonModuleUtil.classNameSerializerModule` is module responsible for serializing class property. It accepts two arguments first decides if class property should be serialzied for classes annotated
with `javax.persistence.Entity` annotation or not. Second is (optional) additional package list whose class property should also be serialized.

## Usage

When registered on `ObjectMapper` these modules are used automatically when serializing and deserializing JSON content.
