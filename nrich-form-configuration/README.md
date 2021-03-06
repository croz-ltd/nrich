# nrich-form-configuration

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-form-configuration/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-form-configuration)

## Overview

`nrich-form-configuration` is a module intended to provide a way of resolving server side defined constraints to client side. It resolves `jakarta-validation-api`
constraints defined on classes in a form that can be interpreted by the client side. On server side user registers form id (a string) with class holding constraints and then resolves defined
constraint list from client side using REST API. Messages for constraints are resolved through Spring's `MessageSource`.

## Setting up Spring beans

To be able to use this module following configuration is required:

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

`FieldErrorMessageResolverService` is responsible for resolving messages for constraints (i.e. 'Value cannot be null' for @NotNull constraint). Default implementation
is `MessageSourceFieldErrorMessageResolverService` that resolves messages from `MessageSource`. For example for constraint holding class of
type `net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedTestRequest` and `@NotNull` constraint defined on property named `propertyName` following message codes will be
searched:

- `net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedTestRequest.propertyName.client.NotNull.invalid`
- `formConfigurationServiceNestedTestRequest.propertyName.client.NotNull.invalid`
- `net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedTestRequest.propertyName.NotNull.invalid`
- `formConfigurationServiceNestedTestRequest.propertyName.NotNull.invalid`
- `client.NotNull.invalid`
- `NotNull.invalid`

`ConstrainedPropertyValidatorConverterService` is service responsible for converting constraints in a format client can interpret. Default implementation
is `DefaultConstrainedPropertyValidatorConverterService` but users can register their own by implementing `ConstrainedPropertyValidatorConverterService` interface.

`ConstrainedPropertyValidatorConverterService` returns a list of `ConstrainedPropertyClientValidatorConfiguration` for each constraint
(a list since some constraints on server may translate to multiple constraints on the client).

`FormConfigurationService` is service that processes constraints defined on a class for form id list and returns a list of
`FormConfiguration` instances holding client side constrained property configuration.

`FormConfigurationController` is REST API exposed to client. It has a single POST method `fetch` mapped to `nrich/form/configuration/fetch` that accepts a request holding form id list in
property `formIdList` and it returns a list of `FormConfiguration` instances.

## Usage

On server side users should register form id with class that is used to bind values submitted from that form. On client side REST API endpoint for fetching form configuration should be
called (`nrich/form/configuration/fetch`) and received response should be converted to client side constraints and applied to field defined on the form.

For following request:

```java

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

Response sent from server is in following form:

```json
[
    {
        "formId": "demo.EmployeeForm",
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
