# nrich-encrypt

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-encrypt/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-encrypt)

## Overview

This module can help in situations when sensitive data needs to be encrypted before it is returned (to the client side of a webapp for example) and then decrypted before it is used again. It provides
the possibility of encrypting and decrypting whole objects of data or only parts of them. Currently, only Strings are supported for encryption and decryption.

Let's say you have a REST service in your webapp that returns user details data. This service call could look like this for example

**GET** _**http://exampleapp/user/23**_

Everyone using this service can easily assumme that 23 is some kind of ID by which that user data is identified in the database for example. At that point if you don't have any ACL methods implemented
in your service someone can change that ID and get some other users data that it shouldn't be able to do.

**GET** _**http://exampleapp/user/24**_

**GET** _**http://exampleapp/user/25**_

**GET** _**http://exampleapp/user/345**_

If you use this service call to load user details data in a web form for example and than use a second service call to store changes of user details in the database like this one

**POST** _**http://exampleapp/user/23**_

then a client could easily change that ID manually before form submit and overwrite some other users' data in the database.
**This kind of security issues can be solved by encrypting critical data, like ID above, before it is returned to the client and then by decrypting it when it is submitted to the server. Using that
approach your service call could look like this**

**POST** **_http://exampleapp/user/535fa30d7e25dd8a49f1536779734ec828610_**

**Now if someone tries to tamper with that the server side will know it end return error.
`nrich-encrypt` module provides this functionality and it is very easy to use.**

Data encryption/decryption can be used in two ways. By using annotations (**_@EncryptResult_**, **_@DecryptArgument_**) or by specifying fully qualified method names (class name + method name) in
configuration classes.

Default implementation uses **AesBytesEncryptor** from **Spring Security** library but can be overridden if needed.

## Usage with Spring Boot

If you are using Spring Boot in your project then the easiest way to configure `nrich-encrypt` is by using `nrich-encrypt-spring-boot-starter`. All you need to do is add it to your Gradle dependencies
configuration.

```groovy
dependencies {
    implementation 'net.croz.nrich:nrich-encrypt-spring-boot-starter:0.0.1'
}
```

This will configure all the necessary default beans needed for `nrich-encrypt` to work automatically.

In our example we are going to use following `Account` class

```java

public class Account {

    private String id;

    private String number;

    private String name;

    private BigDecimal balance;

}

```

As mentioned above data encryption/decryption can be used in two ways. By using annotations `@EncryptResult`, `@DecryptArgument` or by using configuration classes. In our example we are going to use
annotations. Lets say that `id` field on our `Account` instances holds sensitive information and that we don't want to expose it to our clients. In that case we could have `AccountController` with two
endpoints (one for retrieving and one for updating account data) that looks like this

```java

@RequiredArgsConstructor
@RequestMapping("account")
@RestController
public class AccountController {

    private final AccountService accountService;

    @GetMapping()
    @EncryptResult(resultPathList = "id")
    public List<Account> listAll() {

        return accountService.getAccounts();
    }

    @PutMapping()
    @EncryptResult(resultPathList = "id")
    public Account save(@RequestBody @DecryptArgument(argumentPathList = "id") Account account) {

        return accountService.save(account);
    }
}

```

`@EncryptResult` annotation is used to encrypt result of method execution. Data that is encrypted is specified in property named: `resultPathList`, it contains a list of paths to properties that
should be encrypted. Path list supports collections and plain properties also allowing data encryption for `CompletableFurure`, `Mono` and `Flux` instances. So for example if property inside list
named `listOfObjects`
with name: `propertyToEncrypt` should be encrypted then  `resultPathList` should have the value of: `"listOfObjects.propertyToEncrypt"`.

`@DecryptArgument` annotation is used to decrypt method arguments that have been previously encrypted. Arguments that are decrypted should have been encrypted with same service and password/salt
combination that has been used for encryption. Data that is decrypted is specified in property `argumentPathList`. Path list also supports collections and plain properties. Syntax is same as
for `@EncryptResult` annotation.

## Setting up Spring beans

To use `nrich-encrypt` module in your plain Spring project(see below for Spring Boot) first you need to add the following dependency to your Gradle configuration.

```groovy

dependencies {
    implementation 'net.croz.nrich:nrich-encrypt:0.0.1'
}
```

After that to be able to use this module **AspectJ** should be enabled (i.e. by adding `@EnableAspectJAutoProxy` on `@Configuration` class) and following beans should be defined in context:

```java

@Configuration
@EnableAspectJAutoProxy
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Bean
    public TextEncryptionService textEncryptionService() {
        BytesEncryptor encryptor = Encryptors.standard(KeyGenerators.string().generateKey(), KeyGenerators.string().generateKey());

        return new BytesEncryptorTextEncryptService(encryptor, "UTF-8");
    }

    @Bean
    public DataEncryptionService dataEncryptionService(TextEncryptionService textEncryptionService) {
        return new DefaultDataEncryptService(textEncryptionService);
    }
}

```

Default implementation of `TextEncryptionService` is `BytesEncryptorTextEncryptService` and uses Spring's BytesEncryptor that is passed in as argument for text encryption. `BytesEncryptor` can be
created with fixed password and salt or using random generated on each restart (in former case encrypted data should not be persisted). It also accepts charset used for converting byte array to string
and vice versa.

Default implementation of `DataEncryptionService` is `DefaultDataEncryptService`. This class is responsible for resolving data to be encrypted/decrypted from method arguments or return value. It is
also responsible for calling `TextEncryptionService` to perform actual encryption/decryption.

If encryption or decryption fails `EncryptionOperationFailedException` exception is thrown and original exception can be found in the cause (in default encryption service
implementation `BytesEncryptorTextEncryptService`).

To be able to use `nrich-encrypt` with annotations on methods `EncryptDataAspect` bean has to be defined in your Spring configuration.

```java

@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {

    @Bean
    public EncryptDataAspect encryptDataAspect(DataEncryptionService dataEncryptionService) {
        return new EncryptDataAspect(dataEncryptionService);
    }
}

```

After that you can use `@EncryptResult` and `@DecryptArgument` annotations on your methods same as in Spring Boot example above.

## Encrypt and Decrypt using Spring configuration

Module can also be used by specifying fully qualified method names and additional arguments using `EncryptionConfiguration` list.

`EncryptionConfiguration` consists of the following data:

- `methodToEncryptDecrypt` - method that should be encrypted/decrypted containing fully qualified method name i.e. for class com.Example and method print it should be com.Example.print

- `propertyToEncryptDecryptList` - a list of property paths to encrypt or decrypt

- `encryptionOperation` - enum indicating encryption operation can be either `ENCRYPT` or `DECRYPT`

So instead of using `@EncryptResult` and `@DecryptArgument` annotations and `encryptDataAspect` @Bean above we could be using following configuration for our `AccoutController`

```java

@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {

    @Bean
    public Advisor encryptorAdvisor(DataEncryptionService dataEncryptionService) {

        List<EncryptionConfiguration> encryptionConfigurationList = Arrays.asList(
            new EncryptionConfiguration("com.example.demo.controller.AccountController.listAll", Collections.singletonList("id"), EncryptionOperation.ENCRYPT),
            new EncryptionConfiguration("com.example.demo.controller.AccountController.save", Collections.singletonList("id"), EncryptionOperation.ENCRYPT),
            new EncryptionConfiguration("com.example.demo.controller.AccountController.save", Collections.singletonList("id"), EncryptionOperation.DECRYPT)
        );

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        pointcut.setExpression(PointcutResolvingUtil.resolvePointcutFromEncryptionConfigurationList(encryptionConfigurationList));

        return new DefaultPointcutAdvisor(pointcut, new EncryptMethodInterceptor(dataEncryptionService, encryptionConfigurationList, null));
    }
}

```

This configuration produces the same encryption and decryption results as in example with using annotations above.

## Conclusion ##

In this article we saw two ways to use `nrich-encrypt` features for data encryption and decryption in ou apps.

* Easiest way is with Spring Boot starter `nrich-encrypt-spring-boot-starter` in combination with `@EncryptResult` and `@DecryptArgument` annotations
* Instead of annotations `EncryptionConfiguration` can be used
* `nrich-encrypt` can be used in plain Spring applications but `TextEncryptionService`, `DataEncryptionService` and `EncryptDataAspect` or `encryptorAdvisor` need to be defined manually.
