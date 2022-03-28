# nrich-search-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-search-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-search-spring-boot-starter)

## Overview

Spring Boot starter for `nrich-search` module. The purpose of `nrich-search` is to provide easy querying of JPA entities through automatic query creation from passed parameters. It is built on top
of `spring-data-jpa` module and adds custom repository interfaces whose implementation provides searching functionality. Searching is possible with a simple string and a list of searched properties or
by using a separate class whose properties will be used to create a query. Additional configuration of generated query is possible with `SearchConfiguration` configuration class.
Starter module provides a `@Configuration` class (`NrichSearchAutoConfiguration`) with default configuration of `nrich-search` module (while allowing for overriding with conditional annotations),
and a `@ConfigurationProperties` class (`NrichSearchProperties`) with default configured values and does automatic registration through `spring.factories`.

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

Note if using `nrich-bom` dependency versions should be omitted.

### Configuration

Configuration is done through a property file, available properties and descriptions are given bellow (all properties are prefixed with nrich.search which is omitted for readability):

| property                                  | description                                                                                                                | default value                                            |
|-------------------------------------------|----------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------|
| default-converter-enabled                 | Whether default string to type converter used for converting strings to property values when searching registry is enabled | true                                                     |
| string-search.date-format-list            | List of date formats used to convert string to date value                                                                  | dd.MM.yyyy., dd.MM.yyyy.'T'HH:mm, dd.MM.yyyy.'T'HH:mm'Z' |
| string-search.decimal-number-format-list  | List of decimal formats used to convert string to decimal value                                                            | #0.00, #0,00                                             |
| string-search.boolean-true-regex-pattern  | Regexp pattern that is used to match boolean true values                                                                   | ^(?i)\s*(true&#124;yes&#124;da)\s*$                      |
| string-search.boolean-false-regex-pattern | Regexp pattern that is used to match boolean false values                                                                  | ^(?i)\s*(false&#124;no&#124;ne)\s*$                      |

The properties under string-search are used when converting string received from client to property values that will be used to search entity. For example if a string is sent and the property
searched by is of type Date, nrich will try to parse string to Date and if parsing succeeds restriction will be added to query and if parsing fails the property will be skipped (no exception is thrown
or logged).

The default configuration values in yaml format for easier modification are given bellow:

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

Module provides `SearchExecutor<T>` interface that enables searching by properties defined in a class and `StringSearchExecutor<T>` interface that enables searching by a string and provided list of
properties to search. WHen using Hibernate as JPA implementation `NaturalIdSearchExecutor` is also available that enables searching by `@NaturalId` annotated properties.

To use the module users should first enable custom repository factory:

```java

@EnableJpaRepositories(repositoryFactoryBeanClass = SearchExecutorJpaRepositoryFactoryBean.class)
@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {

}

```

After that implement one or both interfaces in a spring-data repository:

```java

@Repository
public interface UserSearchRepository extends JpaRepository<User, Long>, SearchExecutor<User>, StringSearchExecutor<User>, NaturalIdSearchExecutor<User> {

}

```

As a result, new repository method becomes available for searching:

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
