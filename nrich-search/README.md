# nrich-search

## Overview

nrich-search is a library whose purpose is to make querying of JPA entities easier. Motivation for it creation were complex search form
on multiple projects. It build on top of Spring Data JPA library and takes care of query composition from classes holding property values
and addition `SearchConfigration` that defines how those property values should be applied (i.e. what operator should be used), what clases should
be returned etc.   
 

## Setting up Spring beans

To be able to use this library Jpa repositories should be enabled with custom factory by placing `@EnableJpaRepositories(repositoryFactoryBeanClass = SearchExecutorJpaRepositoryFactoryBean.class)` annotation
on `@Configuration` class and following beans should be defined in context:

```

    @Bean
    public StringToTypeConverter<?> defaultStringToTypeConverter() {
        return new DefaultStringToTypeConverter(Arrays.asList("dd.MM.yyyy", "yyyy-MM-dd'T'HH:mm"), Arrays.asList("#0.00", "#0,00"), "^(?i)\\s*(true|yes)\\s*$", "^(?i)\\s*(false|no)\\s*$");
    }

    @Bean
    public StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter(final List<StringToTypeConverter<?>> stringToTypeConverterList) {
        return new DefaultStringToEntityPropertyMapConverter(stringToTypeConverterList);
    }

    @Bean
    public RepositoryFactorySupportFactory searchRepositoryFactorySupportFactory(final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter) {
        return new SearchRepositoryFactorySupportFactory(stringToEntityPropertyMapConverter);
    }

```
`StringToTypeConverter<?>` performs conversion from string to typed instances and is used when querying registry entities. Default implementation (`DefaultStringToTypeConverter`)
accepts a list of data formats and regexes that are used to convert string to types found in properties of entity classes.

`StringToEntityPropertyMapConverter` is also used for querying registry entities, it is responsible for assembling conditions Map from query string and a list of properties to search (
conversion to typed instances is delegated to `StringToTypeConverter<?>`). When searching using `StringSearchExecutor` it accepts a query string, and a list of properties to be searched that
are then converted using  `StringToEntityPropertyMapConverter` to properties.

`RepositoryFactorySupportFactory` implementation `SearchRepositoryFactorySupportFactory` is reponsible for creating backing classes for repository that implement `StringSearchExecutor` or 
`SearchExecutor` interfaces.  

## Usage

Users have two interfaces available for usage `SearchExecutor` and `StringSearchExecutor`. First accepts a class holding properties that will be used for query creation and `SearchConfiguration` and
second accepts a query string, list of properties for search and `SearchConfiguration`, later is best used for quick search functionality and former for standard search form functionality.
Since this library augments Spring Data JPA in their own repository interfaces users should also implement one of Spring Data JPA repository interfaces.

A custom repository interface for searching entities of type Car:

```

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

looks like this (if both standard and quick search functionality is required) 

```


public interface CarRepository extends JpaRepository<Car, Long>, SearchExecutor<Car>, StringSearchExecutor<Car> {

}


```  

When searching for Car instances using `SearchExecutor<Car>` it is necessary to define a class holding all properties that will be searched in this case that would look something like this 
(`BaseSortablePageableRequest` is a class provided by library but it is not strictly necessary to extend it just makes it easier to handle paging and sorting parameters):

```

@Setter
@Getter
public class SearchCarRequest extends BaseSortablePageableRequest {

    private String registrationNumber;

    private Date manufacturedDateFrom;

    private Date manufacturedDateTo;

    private BigDecimal priceFrom;

    private BigDecimal priceTo;

    private Integer numberOfKilometers;

    private String carTypeMake;

    private String carTypeModel;

}

```

Assuming we want to return a projection instead of Car entities then we would also define a projection class.



```

@RequiredArgsConstructor
@Getter
public class CarSearchResult {

    private final String registrationNumber;

    private final Date manufacturedDate;

    private final BigDecimal price;

    private final Integer numberOfKilometers;

    @Projection(path = "carType.make")
    private final String carTypeMake;

    @Projection(path = "carType.make")
    private final String carTypeModel;
}


```

Then in our service we would invoke it like this:


```

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CarSearchDemoService {

    private final CarRepository carRepository;

    public Page<CarSearchResult> search(final SearchCarRequest request) {
        final SearchConfiguration<Car, CarSearchResult, SearchCarRequest> searchConfiguration = SearchConfiguration.<Car, CarSearchResult, SearchCarRequest>builder().resolvePropertyMappingUsingPrefix(true).resultClass(CarSearchResult.class).build();

        return carRepository.findAll(request, searchConfiguration, PageableUtil.convertToPageable(request));
    }

    public List<CarSearchResult> simpleSearch(final String query) {
        final SearchConfiguration<Car, CarSearchResult, Map<String, Object>> searchConfiguration = SearchConfiguration.<Car, CarSearchResult, Map<String, Object>>builder().resolvePropertyMappingUsingPrefix(true).resultClass(CarSearchResult.class).build();

        return carSearchRepository.findAll(query, Arrays.asList("registrationNumber", "price"), searchConfiguration);
    }
}


```

This `SearchConfiguration` maps fields by property path, so for example value in carTypeMake is searched on car.carType.make. 
From and To suffixes represent range search so `manufacturedDateFrom` searches `manufacturedDate` property on Car that is greater than the one
in `SearchCarRequest`. CarSearchResult matches results by property name and when using properties from association then
`@Projection` annotation should be defined with path to property.
When using `simpleSearch` method we are searching Car entity by properties `registrationNumber` and `price`. When properties is not a String type
conversion is attempted using `StringToTypeConverter` and if it succeeds then property is searched otherwise it is ignored.

`SearchConfiguration` has a lot of options that users can customize searching and result resolving. For instance if we will be returning a regular class instead of 
projection `.resultClass(CarSearchResult.class)` can be omitted and return class will be resolved from repository type parameter.
Instead of a result class users can also manully define a list of projections using `projectionList` property, users can define joins (fetches) (so no data is fetched in one select) 
by using `joinList`. `resolvePropertyMappingUsingPrefix` resolves properties by prefix but it is also possible to write explicit property mapping from search request to searched entity by using
`propertyMappingList`. Default operators (see `DefaultSearchOperator`) are for String ILIKE (uses `criteriaBuilder.like` with lower call before), for range search GT (uses `criteriaBuilder.greaterThan`), LT (uses `criteriaBuilder.lessThan`), GE (uses `criteriaBuilder.greaterThanOrEqualTo`), LE (uses `criteriaBuilder.lessThanOrEqualTo`) and for all other classes EQ but this cane be overriden
either on type level or on property level by using `searchOperatorOverrideList`.  Queries on plural associations are done using exists query (to avoid duplicate results) but that can be overriden
by property `pluralAssociationRestrictionType`.  Additional restrictions (not dependent on data in search request, for example security restrictions) can be specified using `additionalRestrictionResolverList`.
When searching also by entity that doesn't have a direct association to root entity `subqueryConfigurationList` property needs to be defined.
`SearchConfigration` also supports matching any value (or operator is used when creating query) by setting `anyMatch` parameter to `true` or matching all values (default behaviour) and operator is used for query. 

`subqueryConfigurationList` is useful when we want to search entities without direct association i.e. if we would like to search User based on a Role and 
User has no direct association to role instead we mapped that connection by using UserRole entity.
  
```

    final SubqueryConfiguration subqueryConfiguration = SubqueryConfiguration.builder()
                .rootEntity(UserRole.class)
                .propertyPrefix("userRole")
                .joinBy(new SearchPropertyJoin("id", "user.id")).build();

    final SearchConfiguration<User, User, UserSearchRequest> searchConfiguration = SearchConfiguration.<User, User, UserSearchRequest>builder()
                .subqueryConfigurationList(Collections.singletonList(subqueryConfiguration))
                .build();


```

This configuration will search `UserRole` entity by all properties in `UserSearchRequest` that have a prefix `userRole`.
