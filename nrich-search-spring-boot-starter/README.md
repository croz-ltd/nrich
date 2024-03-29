# nrich-search-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-search-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-search-spring-boot-starter)

## Overview

This module is a Spring Boot starter for the [`nrich-search`][nrich-search-url] module.
The purpose of [`nrich-search`][nrich-search-url] is to provide easy querying of JPA entities through automatic query creation from passed parameters.
It's built on top of the Spring Data JPA library and adds custom repository interfaces whose implementation provides searching functionality.
Searching is possible with a simple string and a list of searched properties or by using a separate class whose properties will be used to create a query.
Additional configuration of the generated query is possible with [`SearchConfiguration`][search-configuration-url] configuration class.

Starter module provides a `@Configuration` class ([`NrichSearchAutoConfiguration`][nrich-search-auto-configuration-url]) with default configuration of [`nrich-search`][nrich-search-url] module,
a `@ConfigurationProperties` class([`NrichSearchProperties`][nrich-search-properties-url]) with default configuration values and does automatic bean registration with `spring.factories`.
The Configuration class permits overriding with the help of conditional annotations.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository](https://search.maven.org/). To include the dependency use the following configurations.

With Maven:

```xml
<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-search-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>
```

With Gradle:

```groovy
implementation "net.croz.nrich:nrich-search-spring-boot-starter:${nrich.version}"
```

Note if using [`nrich-bom`][nrich-bom-url] dependency versions should be omitted.

### Configuration

The configuration is done through a property file. Available properties and descriptions are given bellow (all properties are prefixed with **nrich.search** which is omitted for readability):

| property                                  | description                                                                                                                | default value                                            |
|-------------------------------------------|----------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------|
| default-converter-enabled                 | whether default string to type converter used for converting strings to property values when searching registry is enabled | true                                                     |
| string-search.date-format-list            | list of date formats used to convert string to date value                                                                  | dd.MM.yyyy., dd.MM.yyyy.'T'HH:mm, dd.MM.yyyy.'T'HH:mm'Z' |
| string-search.decimal-number-format-list  | list of decimal formats used to convert string to decimal value                                                            | #0.00, #0,00                                             |
| string-search.boolean-true-regex-pattern  | regular expression that is used to match boolean true values                                                               | ^(?i)\s*(true&#124;yes&#124;da)\s*$                      |
| string-search.boolean-false-regex-pattern | regular expression that is used to match boolean false values                                                              | ^(?i)\s*(false&#124;no&#124;ne)\s*$                      |

The properties under string-search are used when converting string received from client to property values that will be used to search entity.
For example, if a string is sent and the property searched by is of type Date, nrich will try to parse string to Date and if parsing succeeds
restriction will be added to query and if parsing fails the property will be skipped (no exception is thrown or logged).

The default configuration values are given bellow in a yaml format for easier modification:

```yaml
nrich.search:
    default-read-only-property-list:
    default-converter-enabled: true
    string-search:
        date-format-list: dd.MM.yyyy., dd.MM.yyyy.'T'HH:mm, dd.MM.yyyy.'T'HH:mm'Z'
        decimal-number-format-list: #0.00, #0,00
        boolean-true-regex-pattern: ^(?i)\s*(true|yes|da)\s*$
        boolean-false-regex-pattern: ^(?i)\s*(false|no|ne)\s*$
```

### Using the module

[`nrich-search`][nrich-search-url] module provides three interfaces:

- [`SearchExecutor`][search-executor-url]
- [`StringSearchExecutor`][string-search-executor-url]
- [`NaturalIdSearchExecutor`][natural-id-search-executor-url]

[`SearchExecutor`][search-executor-url] interface enables searching by properties defined in a class and [`StringSearchExecutor`][string-search-executor-url] interface enables searching by
a string and a provided list of properties. [`NaturalIdSearchExecutor`][natural-id-search-executor-url] interface enables searching by `@NaturalId` annotated properties, but is only available when
Hibernate is used as JPA implementation.

To use the module users should first enable custom repository factory bean:

```java
@EnableJpaRepositories(repositoryFactoryBeanClass = SearchExecutorJpaRepositoryFactoryBean.class)
@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {

}
```

After that, users need to implement one or both interfaces in a spring-data repository:

```java
@Repository
public interface UserSearchRepository extends JpaRepository<User, Long>, SearchExecutor<User>, StringSearchExecutor<User>, NaturalIdSearchExecutor<User> {

}
```

As a result, new repository search method becomes available for searching:

```java
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserSearchRepository userSearchRepository;

    @Transactional(readOnly = true)
    public List<User> findByRequest(UserSearchRequest request) {
        return testEntitySearchRepository.findAll(request, SearchConfiguration.emptyConfiguration());
    }

}
```

[//]: # (Reference links for readability)

[nrich-search-url]: ../nrich-search/README.md

[search-configuration-url]: ../nrich-search-api/src/main/java/net/croz/nrich/search/api/model/SearchConfiguration.java

[nrich-search-auto-configuration-url]: src/main/java/net/croz/nrich/search/starter/configuration/NrichSearchAutoConfiguration.java

[nrich-search-properties-url]: ../nrich-search-spring-boot-starter/src/main/java/net/croz/nrich/search/starter/properties/NrichSearchProperties.java

[nrich-bom-url]: ../nrich-bom/README.md

[search-executor-url]: ../nrich-search-repository-api/src/main/java/net/croz/nrich/search/api/repository/SearchExecutor.java

[string-search-executor-url]: ../nrich-search-repository-api/src/main/java/net/croz/nrich/search/api/repository/StringSearchExecutor.java

[natural-id-search-executor-url]: ../nrich-search-repository-api/src/main/java/net/croz/nrich/search/api/repository/NaturalIdSearchExecutor.java
