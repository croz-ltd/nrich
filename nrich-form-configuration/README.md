# nrich-form-configuration

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-form-configuration/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-form-configuration)

## Overview

`nrich-form-configuration` is a module intended to provide a central place for constraint definitions.
It resolves `jakarta-validation-api` constraints defined on classes in a form that can be interpreted by the client-side. On the server-side user registers a form id (a string) to a class that
defines constraints and can then retrieve the resolved constraint list via REST API. Messages for constraints are resolved through Spring's `MessageSource`.
Both manual form registration or automatic by using [`@FormValidationConfiguration`][form-configuration-annotation-url] annotation on classes are supported.

## Setting up Spring beans

To be able to use this module with manual registration following configuration is required:

```java
@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {

    @Bean
    public FieldErrorMessageResolverService fieldErrorMessageResolverService(MessageSource messageSource) {
        return new MessageSourceFieldErrorMessageResolverService(messageSource);
    }

    @Bean
    public ConstrainedPropertyValidatorConverterService constrainedPropertyValidatorConverterService(FieldErrorMessageResolverService fieldErrorMessageResolverService) {
        return new DefaultConstrainedPropertyValidatorConverterService(fieldErrorMessageResolverService);
    }

    @Bean
    public FormConfigurationService formConfigurationService(LocalValidatorFactoryBean validator, List<ConstrainedPropertyValidatorConverterService> constrainedPropertyValidatorConverterServiceList) {
        Map<String, Class<?>> formIdConstraintHolderMap = new LinkedHashMap<>();

        formIdConstraintHolderMap.put("testRequest.formId", FormConfigurationServiceTestRequest.class);

        return new DefaultFormConfigurationService(validator.getValidator(), formIdConstraintHolderMap, constrainedPropertyValidatorConverterServiceList);
    }

    @Bean
    public FormConfigurationController formConfigurationController(FormConfigurationService formConfigurationService) {
        return new FormConfigurationController(formConfigurationService);
    }
}
```

To be able to use this module with automatic registration following configuration is required:

```java
@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {

    @Bean
    public FieldErrorMessageResolverService fieldErrorMessageResolverService(MessageSource messageSource) {
        return new MessageSourceFieldErrorMessageResolverService(messageSource);
    }

    @Bean
    public ConstrainedPropertyValidatorConverterService constrainedPropertyValidatorConverterService(FieldErrorMessageResolverService fieldErrorMessageResolverService) {
        return new DefaultConstrainedPropertyValidatorConverterService(fieldErrorMessageResolverService);
    }

    @Bean
    public FormConfigurationAnnotationResolvingService formConfigurationAnnotationResolvingService() {
        return new DefaultFormConfigurationAnnotationResolvingService();
    }


    @Bean
    public FormConfigurationService formConfigurationService(LocalValidatorFactoryBean validator, FormConfigurationAnnotationResolvingService formConfigurationAnnotationResolvingService,
                                                             List<ConstrainedPropertyValidatorConverterService> constrainedPropertyValidatorConverterServiceList) {
        Map<String, Class<?>> formIdConstraintHolderMap = new LinkedHashMap<>(formConfigurationAnnotationResolvingService.resolveFormConfigurations(Collections.singletonList("net.croz")));

        return new DefaultFormConfigurationService(validator.getValidator(), formIdConstraintHolderMap, constrainedPropertyValidatorConverterServiceList);
    }

    @Bean
    public FormConfigurationController formConfigurationController(FormConfigurationService formConfigurationService) {
        return new FormConfigurationController(formConfigurationService);
    }
}
```

## FieldErrorMessageResolverService

[`FieldErrorMessageResolverService`][field-error-message-resolver-service-url] is responsible for resolving messages for constraints (i.e. 'Value cannot be null' for @NotNull constraint).
Default implementation is [`MessageSourceFieldErrorMessageResolverService`][message-source-field-error-message-resolver-service-url] that resolves messages from `MessageSource`.

For example, for class:

```java
package net.croz.nrich.formconfiguration.stub;

public class FormConfigurationServiceNestedTestRequest {

    @NotNull
    private String propertyName;

}
```

the following message codes will be searched:

- `net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedTestRequest.propertyName.client.NotNull.invalid`
- `formConfigurationServiceNestedTestRequest.propertyName.client.NotNull.invalid`
- `net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedTestRequest.propertyName.NotNull.invalid`
- `formConfigurationServiceNestedTestRequest.propertyName.NotNull.invalid`
- `client.propertyName.NotNull.invalid`
- `propertyName.NotNull.invalid`
- `client.NotNull.invalid`
- `NotNull.invalid`

---

## ConstrainedPropertyValidatorConverterService

[`ConstrainedPropertyValidatorConverterService`][constrained-property-validator-converter-service-url] is responsible for converting constraints in a format the client can interpret.
Default implementation is [`DefaultConstrainedPropertyValidatorConverterService`][default-constrained-property-validator-converter-service-url] but users can register their own by implementing
[`ConstrainedPropertyValidatorConverterService`][constrained-property-validator-converter-service-url] interface.

[`ConstrainedPropertyValidatorConverterService`][constrained-property-validator-converter-service-url] returns a list of
[`ConstrainedPropertyClientValidatorConfiguration`][constrained-property-client-validator-configuration-url] for each constraint (a list since some constraints on server may translate to multiple
constraints on the client).

---

## FormConfigurationService

[`FormConfigurationService`][form-configuration-service-url] processes constraints defined on a class for form id list and returns a list of [`FormConfiguration`][form-configuration-url] instances
holding client-side constrained property configuration.

---

## FormConfigurationAnnotationResolvingService

[`FormConfigurationAnnotationResolvingService`][form-configuration-annotation-resolving-service-url] scans provided packages for classes annotated with
[`@FormValidationConfiguration`][form-configuration-annotation-url] and constructs a map where keys are form ids and values are annotated classes that contain constraints which can then be used by
[`FormConfigurationService`][form-configuration-service-url].

---

## FormConfigurationController

[`FormConfigurationController`][form-configuration-controller-url] is REST API exposed to the client.

It has two POST methods:

- `fetch` mapped to `nrich/form/configuration/fetch` that accepts a request whose body has a form id list in property `formIdList` and it returns a list of
  [`FormConfiguration`][form-configuration-url] instances
- `fetch-all` mapped to `nrich/form/configuration/fetch-all` that returns a list of [`FormConfiguration`][form-configuration-url] instances for every registered form.

REST API base path of the URL can be changed by setting a custom `nrich.form-configuration.endpoint-path` property value.

## Usage

On the server-side users should register form id with class that is used to bind values submitted from that form. On the client-side the REST API endpoint for fetching form configuration should be
called (`nrich/form/configuration/fetch`) and received response should be converted to client-side constraints and applied to fields defined on the form.

For the following request:

```java
package example;

@Setter
@Getter
public class EmployeeRequest {

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Date startDate;

    @NotNull
    private Date endDate;

    @Size(min = 3, max = 3)
    private String phone1;

    @Max(23)
    @Min(0)
    private Integer hours;

    private String title;

    @NotBlank
    private String firstName;

    @DecimalMin("0.0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal income;

}
```

The above request can be registered manually by adding it to the map and passing it to [`FormConfigurationService`][form-configuration-service-url] where map key is for example "example.form" and value is request class.
It can alternatively be annotated with  [`@FormValidationConfiguration`][form-configuration-annotation-url] annotation i.e.

```java
@Setter
@Getter
@FormValidationConfiguration("example.form")
public class EmployeeRequest {
    // properties omitted for brevity
}


```

For request:

```json
{
    "formIdList": [
        "example.form"
    ]
}
```

the response sent from server is in the following form:

```json
[
    {
        "formId": "example.form",
        "constrainedPropertyConfigurationList": [
            {
                "path": "firstName",
                "propertyType": "java.lang.String",
                "javascriptType": "string",
                "validatorList": [
                    {
                        "name": "NotBlank",
                        "argumentMap": {},
                        "errorMessage": "Cannot be blank"
                    }
                ]
            },
            {
                "path": "email",
                "propertyType": "java.lang.String",
                "javascriptType": "string",
                "validatorList": [
                    {
                        "name": "Email",
                        "argumentMap": {
                            "regexp": ".*",
                            "flags": []
                        },
                        "errorMessage": "Email is not in the correct format"
                    },
                    {
                        "name": "NotBlank",
                        "argumentMap": {},
                        "errorMessage": "Cannot be blank"
                    }
                ]
            },
            {
                "path": "hours",
                "propertyType": "java.lang.Integer",
                "javascriptType": "number",
                "validatorList": [
                    {
                        "name": "Min",
                        "argumentMap": {
                            "value": 0
                        },
                        "errorMessage": "Minimum value is: 0"
                    },
                    {
                        "name": "Max",
                        "argumentMap": {
                            "value": 23
                        },
                        "errorMessage": "Maximum value is: 23"
                    }
                ]
            },
            {
                "path": "income",
                "propertyType": "java.math.BigDecimal",
                "javascriptType": "number",
                "validatorList": [
                    {
                        "name": "Digits",
                        "argumentMap": {
                            "integer": 10,
                            "fraction": 2
                        },
                        "errorMessage": "Maximum number of digits is: 10 and scale is: 2"
                    },
                    {
                        "name": "DecimalMin",
                        "argumentMap": {
                            "inclusive": true,
                            "value": "0.0"
                        },
                        "errorMessage": "Minimum value is: 0.0"
                    }
                ]
            },
            {
                "path": "endDate",
                "propertyType": "java.util.Date",
                "javascriptType": "date",
                "validatorList": [
                    {
                        "name": "NotNull",
                        "argumentMap": {},
                        "errorMessage": "Cannot be null"
                    }
                ]
            },
            {
                "path": "phone1",
                "propertyType": "java.lang.String",
                "javascriptType": "string",
                "validatorList": [
                    {
                        "name": "Size",
                        "argumentMap": {
                            "min": 3,
                            "max": 3
                        },
                        "errorMessage": "Size must be between: 3 and 3"
                    }
                ]
            },
            {
                "path": "startDate",
                "propertyType": "java.util.Date",
                "javascriptType": "date",
                "validatorList": [
                    {
                        "name": "NotNull",
                        "argumentMap": {},
                        "errorMessage": "Cannot be null"
                    }
                ]
            }
        ]
    }
]


```

If user needs to fetch constraint descriptions for all registered forms, `nrich/form/configuration/fetch-all` endpoint should be queried.

[//]: # (Reference links for readability)

[nrich-form-configuration-url]: ../nrich-form-configuration/README.md

[nrich-bom-url]: ../nrich-bom/README.md

[constrained-property-validator-converter-service-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/service/ConstrainedPropertyValidatorConverterService.java

[default-constrained-property-validator-converter-service-url]: ../nrich-form-configuration/src/main/java/net/croz/nrich/formconfiguration/service/DefaultConstrainedPropertyValidatorConverterService.java

[constrained-property-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/model/ConstrainedProperty.java

[constrained-property-client-validator-configuration-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/model/ConstrainedPropertyClientValidatorConfiguration.java

[form-configuration-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/model/FormConfiguration.java

[form-configuration-service-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/service/FormConfigurationService.java

[form-configuration-controller-url]: ../nrich-form-configuration/src/main/java/net/croz/nrich/formconfiguration/controller/FormConfigurationController.java

[field-error-message-resolver-service-url]: ../nrich-form-configuration/src/main/java/net/croz/nrich/formconfiguration/service/FieldErrorMessageResolverService.java

[message-source-field-error-message-resolver-service-url]: ../nrich-form-configuration/src/main/java/net/croz/nrich/formconfiguration/service/MessageSourceFieldErrorMessageResolverService.java

[form-configuration-annotation-resolving-service-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/service/FormConfigurationAnnotationResolvingService.java

[form-configuration-annotation-url]: ../nrich-form-configuration-api/src/main/java/net/croz/nrich/formconfiguration/api/annotation/FormValidationConfiguration.java
