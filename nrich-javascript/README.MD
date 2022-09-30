# nrich-javascript

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-javascript/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-javascript)

## Overview

nrich-javascript is a module that contains classes for conversion between Java class and its Javascript representation. It is used internally by `nrich-form-configuration` and `nrich-registry`
modules.

## Usage

This module is primary internal to nrich. But if users want to customize conversion of Java to Javascript type they can implement [`JavaToJavascriptTypeConverter`][converter-url]
interface and override default behaviour. For default behaviour see [`DefaultJavaToJavascriptTypeConverter`][default-converter-url].

[//]: # (Reference links)

[converter-url]: ../nrich-javascript-api/src/main/java/net/croz/nrich/javascript/api/converter/JavaToJavascriptTypeConverter.java

[default-converter-url]: ../nrich-javascript/src/main/java/net/croz/nrich/javascript/converter/DefaultJavaToJavascriptTypeConverter.java
