# nrich-validation

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-validation/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-validation)

## Overview

`nrich-validation` is a module intended to add additional `jakarta.validation-api` constraints that have proved to be useful on multiple occasions.
It contains a list of constraints and corresponding validators. Registration of validators is tied to `hibernate-validator` implementation.
It also contains a list of messages for standard and additional constraints in english and croatian (`nrich-validation-messages`).

## Setting up Spring beans

If automatic registration of messages is required then following configuration is required
(this can also be registered through application.properties by manually including `validationMessages` when using Spring Boot):

```java

@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {

    private static final String VALIDATION_MESSAGES_NAME = "nrich-validation-messages";

    @Bean
    public ValidationMessageSourceRegistrar validationMessageSourceRegistrar(MessageSource messageSource) {
        return new ValidationMessageSourceRegistrar(messageSource);
    }

    @RequiredArgsConstructor
    public static class ValidationMessageSourceRegistrar implements InitializingBean {

        private MessageSource messageSource;

        @Override
        public void afterPropertiesSet() {
            if (messageSource instanceof AbstractResourceBasedMessageSource) {
                ((AbstractResourceBasedMessageSource) messageSource).addBasenames(VALIDATION_MESSAGES_NAME);
            }
        }
    }
}

```

## Usage

The module provides following constraints:

#### InList

Validates that a property is in a list of String values i.e:

```java

@Setter
@Getter
public class ExampleRequest {

    @InList(value = { "first", "second" })
    private String value;

}

```

will validate that value is either equal to `first` or equal to `second`.

#### MaxSizeInBytes

Validates that value doesn't exceed specified number of bytes in specified encoding (default UTF-8) i.e:

```java

@Setter
@Getter
public class ExampleRequest {

    @MaxSizeInBytes(value = 5)
    private String value;

}

```

will validate that value contains less than or equal to 5 bytes in `UTF-8` encoding.

#### NotNullWhen

Validates that a property is not null when a condition is satisfied i.e:

```java

@Setter
@Getter
@NotNullWhen(property = "firstName", condition = CreatePersonRequest.Condition.class)
public class CreatePersonRequest {

    private String firstName;

    private String lastName;

    public static class Condition implements Predicate<CreatePersonRequest> {
        @Override
        public boolean test(CreatePersonRequest request) {
            return request.getLastName() != null;
        }
    }
}


```

will validate that firstName is not null when lastName is not null (effectively requiring both properties to be null or both properties to be empty).

If Groovy is used, condition can be written as closure.

```groovy
@NotNullWhen(property = "firstName", condition = { request -> request.lastName != null })
class CreatePersonRequest {

    String firstName

    String lastName
}
```

#### NullWhen

Validates that a property is null when a condition is satisfied i.e:

```java

@Setter
@Getter
@NullWHen(property = "companyName", condition = CreateBusinessEntityRequest.Condition.class)
public class CreateBusinessEntityRequest {

    private String companyName;

    private String fistName;

    private String lastName;

    public static class Condition implements Predicate<CreateBusinessEntityRequest> {
        @Override
        public boolean test(CreateBusinessEntityRequest request) {
            return request.getFirstName() != null || request.getLastName() != null;
        }
    }
}


```

will validate that either companyName is filled or firstName or lastName are filled.

If Groovy is used, condition can be written as closure.

```groovy
@NullWHen(property = "companyName", condition = { request -> request.fistName != null || request.lastName != null })
class CreateBusinessEntityRequest {

    String companyName

    String fistName

    String lastName
}
```

#### ValidFile

Validates that file (either Spring's `MultipartFile` or `FilePart`) is in provided content type list, has extension that is in provided extension list and/or satisfies regex. All properties
are optional so defining a constraint without any will always pass validation.

```java

@Setter
@Getter
public class ExampleRequest {

    @ValidFile(allowedContentTypeList = "text/plain", allowedExtensionList = "txt", allowedFileNameRegex = "(?U)[\\w-.]+")
    private MultipartFile file;

}

```

will validate that file has content type of `text/plain`, extension `txt` and matches regex: `(?U)[\\w-.]+`

#### ValidFileResolvable

Does the same thing as `ValidFile` constraint but resolves allowedContentTypeList, allowedExtensionList and allowedFileNameRegex from Spring's `Environment` bean allowing those properties to be
defined in property files.

```java

@Setter
@Getter
public class ExampleRequest {

    @ValidFileResolvable(
        allowedContentTypeListPropertyName = "my.custom.validation.file.allowed-content-type-list",
        allowedExtensionListPropertyName = "my.custom.validation.file.allowed-extension-list",
        allowedFileNameRegexPropertyName = "my.custom.validation.file.allowed-file-name-regex"
    )
    private MultipartFile file;

}

```

will resolve allowedContentTypeList from `my.custom.validation.file.allowed-content-type-list`, allowedExtensionList from `my.custom.validation.file.allowed-extension-list`
and allowedFileNameRegex from `my.custom.validation.file.allowed-file-name-regex` properties and perform validation while skipping resolved empty or null property values.

### ValidOib

Validates that a property is a valid OIB (personal identification number in Croatia).

```java

@Setter
@Getter
public class ExampleRequest {

    @ValidOib
    private String value;

}

```

#### ValidRange

Validates that "from" property (resolved from validated class through fromPropertyName) is less than (or equal to if inclusive flag has been set) "to" property (resolved from validated class through
toPropertyName) i.e:

```java

@Setter
@Getter
@ValidRange(fromPropertyName = "dateFrom", toPropertyName = "dateTo")
public class ValidRangeValidatorDifferentTypeTestRequest {

    private Instant dateFrom;

    private Instant dateTo;

}

```

validates that dateFrom is less than dateTo.

#### ValidSearchProperties

Is useful when it is required that all properties from any group of properties are not null

```java

@Setter
@Getter
@ValidSearchProperties(propertyGroup = { @ValidSearchProperties.PropertyGroup({ "firstName", "lastName" }), @ValidSearchProperties.PropertyGroup("id") })
public class SearchPersonRequest {

    private String firstName;

    private String lastName;

    private Long id;

}

```

Above request will require that either both firstName and lastName are not null or that id is not null.

#### SpelExpression

Validates property against a provided SpEL Expression

```java

@Setter
@Getter
public class ExampleRequest {

    @SpelExpression("@spelValidationTestService.validateUuid(#this)")
    private String uuid;

}

@Setter
@Getter
@SpelExpression("@spelValidationTestService.validateUuid(uuid)")
public class ExampleTypeRequest {

    private String uuid;

}

```

In an example above the property uuid is required to be a valid UUID format. The format check is achieved by calling `validateUuid` method of `spelValidationTestService` bean.

#### MinDate

Validates that the annotated element is after specified minimum date

Supported types are:
- java.util.Date
- java.util.Calendar
- java.time.Instant
- java.time.LocalDate
- java.time.LocalDateTime
- java.time.LocalTime
- java.time.MonthDay
- java.time.OffsetDateTime
- java.time.OffsetTime
- java.time.Year
- java.time.YearMonth
- java.time.ZonedDateTime

```java

@Setter
@Getter
public class ExampleRequest {

    @MinDate("2023-01-01")
    private LocalDate date;

}

```

Above request will require that the date property is after 01.01.2023

#### LastTimestampInDay

Validates that the annotated element is before end of the day

Supported types are:
- java.util.Date
- java.util.Calendar
- java.time.Instant
- java.time.LocalDate
- java.time.LocalDateTime
- java.time.LocalTime
- java.time.MonthDay
- java.time.OffsetDateTime
- java.time.OffsetTime
- java.time.Year
- java.time.YearMonth
- java.time.ZonedDateTime

```java

@Setter
@Getter
public class ExampleRequest {

    @LastTimestampInDay
    private LocalDate date;

}

```

Above request will require that the date property is before the end of the day

Module also adds support for disabling inherited constraints with the following annotation:

#### DisableConstraints

Assuming the following parent class:

```java

@Setter
@Getter
public class ExampleParentRequest {

    @Size(max = 200)
    @NotBlank
    private String name;

    @NotNull
    private Integer age;

}


```

Constraints on name and age can be disabled by either using type annotation (if property name or constraint is not found it will just be ignored):

```java

@DisableConstraints(value = { NotBlank.class, Size.class }, propertyName = "name")
@DisableConstraints(value = NotNull.class, propertyName = "age")
@Setter
@Getter
public class ExampleTypeRequest extends ExampleParentRequest {


}


```

or by using method/property annotation:

```java

public class ExampleMethodRequest extends ExampleParentRequest {

    @DisableConstraints({ NotBlank.class, Size.class })
    @Override
    public String getName() {
        return super.getName();
    }

    @DisableConstraints(NotNull.class)
    @Override
    public Integer getAge() {
        return super.getAge();
    }
}


```
