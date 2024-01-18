# nrich-search

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-search/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-search)

## Overview

`nrich-search` is a module whose purpose is to make querying of JPA entities easier.
Motivation for its creation were complex search forms on multiple projects.
It's built on top of the Spring Data JPA library and takes care of query composition from classes that hold query properties and from
[`SearchConfiguration`][search-configuration-url] class that defines how those query property values should be applied (i.e. what operator should be used, what classes should be returned etc.).

## Setting up Spring beans

To be able to use this module, JPA repositories should be enabled with custom factory by placing `@EnableJpaRepositories(repositoryFactoryBeanClass = SearchExecutorJpaRepositoryFactoryBean.class)`
annotation on `@Configuration` class and needed beans should be defined in context.

`@Configuration` class with enabled JPA repositories and required bean definitions is given bellow.

```java
@EnableJpaRepositories(repositoryFactoryBeanClass = SearchExecutorJpaRepositoryFactoryBean.class)
@Configuration(proxyTargetClass = false)
public class ApplicationConfiguration {

    @Bean
    public StringToTypeConverter<Object> defaultStringToTypeConverter() {
        List<String> dateFormatList = Arrays.asList("dd.MM.yyyy.", "dd.MM.yyyy.'T'HH:mm");
        List<String> decimalFormatList = Arrays.asList("#0.00", "#0,00");
        String booleanTrueRegexPattern = "^(?i)\\s*(true|yes)\\s*$";
        String booleanFalseRegexPattern = "^(?i)\\s*(false|no)\\s*$";

        return new DefaultStringToTypeConverter(dateFormatList, decimalFormatList, booleanTrueRegexPattern, booleanFalseRegexPattern);
    }

    @Bean
    public StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter(List<StringToTypeConverter<?>> stringToTypeConverterList) {
        return new DefaultStringToEntityPropertyMapConverter(stringToTypeConverterList);
    }

    @Bean
    public RepositoryFactorySupportFactory searchRepositoryFactorySupportFactory(StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter) {
        return new SearchRepositoryFactorySupportFactory(stringToEntityPropertyMapConverter);
    }

}
```

### StringToTypeConverter

[`StringToTypeConverter`][string-to-type-converter-url] performs conversion from string to typed instances and is used when querying registry entities.
Default implementation [`DefaultStringToTypeConverter`][default-string-to-type-converter-url] accepts a list of data formats and regular expressions that are used to convert string to the type found
in properties of entity classes.

---

### StringToEntityPropertyMapConverter

[`StringToEntityPropertyMapConverter`][string-to-entity-property-map-converter-url] is also used for querying registry entities. It is responsible for creating conditions from a query string and a
list of properties to search (conversion to typed instances is delegated to [`StringToTypeConverter`][string-to-type-converter-url]). The described conversion takes place when
[`StringSearchExecutor`][search-executor-url] is used to execute the search.

---

### RepositoryFactorySupportFactory

[`RepositoryFactorySupportFactory`][repository-factory-support-factory-url] implementation [`SearchRepositoryFactorySupportFactory`][search-repository-factory-support-factory-url] is responsible for
creating backing classes for repositories that implement [`StringSearchExecutor`][string-search-executor-url] or [`SearchExecutor`][search-executor-url] interface.

## Usage

Users have three interfaces available for usage:

- [`SearchExecutor`][search-executor-url]
- [`StringSearchExecutor`][string-search-executor-url]
- [`NaturalIdSearchExecutor`][natural-id-search-executor-url].

### Using SearchExecutor (standard search)

[`SearchExecutor`][search-executor-url] accepts a query class that will be used for query creation and [`SearchConfiguration`][search-configuration-url] class that decides how query should be
created from defined conditions. It is best used for **standard** search functionality.

To be able to use the [`SearchExecutor`][search-executor-url] the user-defined JPA repository has to extend the [`SearchExecutor`][search-executor-url] interface.

For example, for entity `Car`:

```java
@Setter
@Getter
@Entity
public class Car {

    @GeneratedValue
    @Id
    private Long id;

    private String registrationNumber;

    private Date manufacturedDate;

    private BigDecimal price;

    private Integer numberOfKilometers;

    @ManyToOne(fetch = FetchType.LAZY)
    private CarType carType;

}
```

we have to ensure that its JPA repository extends [`SearchExecutor`][search-executor-url] interface like this:

```java
public interface CarRepository extends JpaRepository<Car, Long>, SearchExecutor<Car> {

}
```

Then, if we need to query car entities by its:

- registration number
- manufacture date (time period from-to)
- price (range query from-to, from included)
- number of kilometers
- car type make
- car type model

we can define a class that describes wanted query parameters:

```java
@Setter
@Getter
public class CarSearchRequest extends BaseSortablePageableRequest {

    private String registrationNumber;

    private Instant manufacturedTimeFrom;

    private Instant manufacturedTimeTo;

    @DecimalMin("0.00")
    private BigDecimal priceFromIncluding;

    @DecimalMin("0.00")
    private BigDecimal priceTo;

    @Min(0)
    private Integer numberOfKilometers;

    @Size(max = 20)
    private String carTypeMake;

    @Size(max = 40)
    private String carTypeModel;

}
```

[`BaseSortablePageableRequest`][base-sortable-pageable-request-url] class makes paging and sorting easier by including page and sort properties. It is not necessary to extend it for search to work.

If a specific result type is required, we can define a projection class:

```java
@RequiredArgsConstructor
@Getter
public class CarSearchResult {

    private final String registrationNumber;

    private final Date manufacturedDate;

    private final BigDecimal price;

    private final Integer numberOfKilometers;

    @Projection(path = "carType.make")
    private final String carTypeMake;

    @Projection(path = "carType.model")
    private final String carTypeModel;

}
```

After search and result class are defined, the service that actually executes the query can be written like this:

```java
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CarSearchService {

    private final CarRepository carRepository;

    public Page<CarSearchResult> search(CarSearchRequest request) {
        SearchConfiguration<Car, CarSearchResult, CarSearchRequest> searchConfiguration = SearchConfiguration.<Car, CarSearchResult, CarSearchRequest>builder()
            .resolvePropertyMappingUsingPrefix(true)
            .resultClass(CarSearchResult.class)
            .build();

        return carRepository.findAll(request, searchConfiguration, PageableUtil.convertToPageable(request));
    }

}
```

In this service we have defined that the query will be executed on the entity `Car` based on the `CarSearchRequest` class and that results of the search will be of type `CarSearchResult`.
This was defined using [`SearchConfiguration`][search-configuration-url] class and its method `.resultClass(CarSearchResult.class)`.
[`SearchConfiguration`][search-configuration-url] class dictates how the query should be formed by performing mappings of properties from defined query class to the properties in the target entity.

From and To suffixes represent range search so `manufacturedDateFrom` searches `manufacturedDate` property on Car that is greater than the one in `CarSearchRequest`.
The same applies to `priceFromIncluding` and `priceTo`, the only difference being that the starting price is included.
[`SearchConfiguration`][search-configuration-url] class maps fields by property path, so for example value in carTypeMake is searched on car.carType.make.

Therefore, resulting query for `CarSearchRequest` request whose values would be :

| Property             | Value       |
|----------------------|-------------|
| registrationNumber   | 14-KR       |
| manufacturedTimeFrom | 22.07.2022. |
| manufacturedTimeTo   | 30.12.2022. |
| priceFromIncluding   | 15          |
| priceTo              | 20          |
| numberOfKilometers   | 100         |
| carTypeMake          | CAR_TYPE    |
| carTypeModel         | CAR_MODEL   |

would be consisted of a right join between entity `Car` and `CarType` (because of association relationship) and a predicate that would be similar to this SQL predicate.

```sql
WHERE manufacturedDate > 22-07-2022 AND manufacturedDate < 30-12-2022 AND price >= 15
AND price < 20 AND numberOfKilometers = 100 AND make = 'CAR_TYPE' AND model = 'CAR_MODEL'
```

`CarSearchResult` matches results by property name and when including properties from association then `@Projection` annotation should be defined with path to property for correct mapping.

```java
@Projection(path = "carType.make")
private final String carTypeMake;

@Projection("carType.model")
private final String carTypeModel;
```

Condition class can be used that will decide if projection should be applied.

```java
@RequiredArgsConstructor
@Getter
public class CarSearchResult {

    @Projection(path = "carType.make", condition = CarTypeCondition)
    private final String carTypeMake;

    static class CarTypeCondition implements Predicate<CarSearchRequest> {

        @Override
        boolean test(CarSearchRequest request) {
            request.registrationNumber != null
        }
    }
}
```

If Groovy is used, condition can be written as closure.

```groovy
class CarSearchResult {

    @Projection(path = "carType.make", condition = { CarSearchRequest request -> request.registrationNumber != null })
    String carTypeMake
}
```

### Using StringSearchExecutor (quick search)

[`StringSearchExecutor`][string-search-executor-url] accepts a query string, list of properties for search and [`SearchConfiguration`][search-configuration-url].
It is best used for **quick** search functionality.

A custom repository interface for searching entities of type Car (same entity that we used in [`SearchExecutor`][search-executor-url] chapter) is given bellow.

```java
public interface CarRepository extends JpaRepository<Car, Long>, StringSearchExecutor<Car> {

}
```

The [`StringSearchExecutor`][string-search-executor-url] query class `StringCarSearchRequest` is different from the `CarSearchRequest` class because it contains a search term and a property list.

```java
@Setter
@Getter
public class StringCarSearchRequest {

    @NotEmpty
    private String searchTerm;

    @Size(min = 1)
    private List<String> propertyToSearchList;

}
```

We can reuse the same result class `CarSearchResult` that was demonstrated in the [`SearchExecutor`][search-executor-url] usage demonstration.

In our service we would invoke it like this:

```java
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CarSearchService {

    private final CarRepository carRepository;

    @Override
    public Page<CarSearchResult> simpleSearch(StringCarSearchRequest request) {
        SearchConfiguration<Car, CarSearchResult, Map<String, Object>> searchConfiguration = SearchConfiguration.<Car, CarSearchResult, Map<String, Object>>builder()
            .joinList(List.of(SearchJoin.innerJoinFetch("carType")))
            .resolvePropertyMappingUsingPrefix(true)
            .resultClass(CarSearchResult.class)
            .anyMatch(true)
            .build();

        return carRepository.findAll(request.getSearchTerm(), request.getPropertyToSearchList(), searchConfiguration, Pageable.unpaged());
    }

}
```

When using `simpleSearch` method we are searching the `Car` entity by properties supplied in `propertyToSearchList`.
When property is not a String type conversion is attempted using [`StringToTypeConverter`][string-to-type-converter-url] and if it succeeds then property is searched otherwise it is ignored.

For example, if request with `searchTerm` value of "22" and `propertyToSearchList` list that consists of `registrationNumber`, `price`, `kilometers` was provided to the `simpleSearch` method,
because of `anyMatch` **or** operator would be used and query predicate would be: `WHERE registrationNumber = 22 OR price = 22`.
Notice that `kilometers` was ignored because `Car` entity has no such property.

### Using NaturalIdSearchExecturor

[`NaturalIdSearchExecturor`][natural-id-search-executor-url] only works with Hibernate JPA implementation and enables users to search entities by properties annotated with `@NaturalId` annotation.
It is useful when entity has, beside its primary key, a natural key (akin to `registrationNumber` in entity `Car`).

### Combining search executors

Users are able to use all types of search executors together on one JPA repository. This is achieved by extending [`SearchExecutor`][search-executor-url],
[`StringSearchExecutor`][string-search-executor-url] and [`NaturalIdSearchExecturor`][natural-id-search-executor-url] in user defined JPA repository.

An example of such a repository would be:

```java
public interface CarSearchDemoRepository extends JpaRepository<Car, Long>, SearchExecutor<Car>, StringSearchExecutor<Car>, NaturalIdSearchExecutor<Car> {

}
```

## SearchConfiguration

[`SearchConfiguration`][search-configuration-url] has a lot of options that let users customize query creation and result resolution.

##### Defining result class

If a regular class needs to be returned instead of projection,`.resultClass(CarSearchResult.class)` can be omitted and return class will be resolved from repository type parameter.
Instead of a result class users can also manually define a list of projections using `projectionList` property.

#### Defining table joins

Users can define joins (fetches) (so no data is fetched in one select) by using `joinList`.
Supported joins are **inner join** and **left outer join**.
Joins are supplied by using the class `SearchJoin`.

#### Defining property mapping

`resolvePropertyMappingUsingPrefix` resolves properties by prefix, but it is also possible to write explicit property mapping from search request to searched entity by using`propertyMappingList`.

#### Default operators

Default operators (see [`DefaultSearchOperator`][default-search-operator-url]) are for String _**ILIKE**_ (uses `criteriaBuilder.like` with lower call before), for range search _**GT**_ (
uses `criteriaBuilder.greaterThan`), _**LT**_ (uses `criteriaBuilder.lessThan`), _**GE**_ (uses `criteriaBuilder.greaterThanOrEqualTo`), _**LE**_ (uses `criteriaBuilder.lessThanOrEqualTo`)
and for all other classes _**EQ**_ but this can be overridden either on type level or on property level by using `searchOperatorOverrideList`.

Queries on plural associations are done using exists query (to avoid duplicate results) but that can be overridden by property `pluralAssociationRestrictionType`.
Additional restrictions (not dependent on data in search request, for example security restrictions) can be specified using `additionalRestrictionResolverList`.

#### Setting **and** / **or** operator

[`SearchConfiguration`][search-configuration-url] supports matching of **all** values (default behaviour), which means that operator and is used for query predicate creation.
[`SearchConfiguration`][search-configuration-url] also supports matching of **any** value by setting `anyMatch` parameter to `true`, which means that or operator is used when creating query predicate.

#### Subquery configuration

`subqueryConfigurationList` is useful when we want to search entities without direct association, i.e. if we would like to search for entity `User` based on a `Role` and `User` has no direct
association to `Role`. It is possible to map that connection by using `UserRole` entity.

```java
    SubqueryConfiguration subqueryConfiguration=SubqueryConfiguration.builder()
    .rootEntity(UserRole.class)
    .propertyPrefix("userRole")
    .joinBy(new SearchPropertyJoin("id","user.id")).build();

    SearchConfiguration<User, User, UserSearchRequest> searchConfiguration=SearchConfiguration.<User, User, UserSearchRequest>builder()
    .subqueryConfigurationList(Collections.singletonList(subqueryConfiguration))
    .build();
```

This configuration will search `UserRole` entity by all properties in `UserSearchRequest` that have a prefix `userRole`.

[//]: # (Reference links for readability)

[search-configuration-url]: ../nrich-search-api/src/main/java/net/croz/nrich/search/api/model/SearchConfiguration.java

[search-executor-url]: ../nrich-search-repository-api/src/main/java/net/croz/nrich/search/api/repository/SearchExecutor.java

[string-search-executor-url]: ../nrich-search-repository-api/src/main/java/net/croz/nrich/search/api/repository/StringSearchExecutor.java

[natural-id-search-executor-url]: ../nrich-search-repository-api/src/main/java/net/croz/nrich/search/api/repository/NaturalIdSearchExecutor.java

[string-to-type-converter-url]: ../nrich-search-api/src/main/java/net/croz/nrich/search/api/converter/StringToTypeConverter.java

[default-string-to-type-converter-url]: ../nrich-search/src/main/java/net/croz/nrich/search/converter/DefaultStringToTypeConverter.java

[string-to-entity-property-map-converter-url]: ../nrich-search-api/src/main/java/net/croz/nrich/search/api/converter/StringToEntityPropertyMapConverter.java

[repository-factory-support-factory-url]: ../nrich-search-repository-api/src/main/java/net/croz/nrich/search/api/factory/RepositoryFactorySupportFactory.java

[search-repository-factory-support-factory-url]: ../nrich-search/src/main/java/net/croz/nrich/search/factory/SearchRepositoryFactorySupportFactory.java

[base-sortable-pageable-request-url]: ../nrich-search-api/src/main/java/net/croz/nrich/search/api/request/BaseSortablePageableRequest.java

[default-search-operator-url]: ../nrich-search-api/src/main/java/net/croz/nrich/search/api/model/operator/DefaultSearchOperator.java
