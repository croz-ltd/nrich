# nrich-encrypt

## Overview

nrich-encrypt is a library intended to enable simple encryption and decryption of sensitive data. Property encryption/decryption can be
enabled by using provided annotations or by specifying fully qualified method names (class name + method name) in configuration classes.
Default implementation for encryption uses AesBytesEncryptor from Spring Security library but can be overridden if needed.
Motivation behind this library is masking sensitive data that is sent to client side but is required for integration between the client and server side 
(i.e. file name in download link, document id from DMS etc.). 

## Setting up Spring beans

To be able to use this library AspectJ should be enabled (i.e. by adding `@EnableAspectJAutoProxy` on `@Configuration` class) and following beans should be defined in context:

```

    @Bean
    public TextEncryptionService textEncryptionService() {
        final BytesEncryptor encryptor = Encryptors.standard(KeyGenerators.string().generateKey(), KeyGenerators.string().generateKey());

        return new BytesEncryptorTextEncryptService(encryptor, "UTF-8");
    }

    @Bean
    public DataEncryptionService dataEncryptionService(final TextEncryptionService textEncryptionService) {
        return new DefaultDataEncryptService(textEncryptionService);
    }

    @Bean
    public EncryptDataAspect encryptDataAspect(final DataEncryptionService dataEncryptionService) {
        return new EncryptDataAspect(dataEncryptionService);
    }

    @Bean
    public Advisor encryptorAdvisor(final DataEncryptionService dataEncryptionService) {
        final List<EncryptionConfiguration> encryptionConfigurationList = Collections.singletonList("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptDataAspectTestService.dataToEncryptFromConfiguration", Collections.singletonList("value"), EncryptionOperation.ENCRYPT);

        final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        pointcut.setExpression(PointcutResolvingUtil.resolvePointcutFromEncryptionConfigurationList(encryptionConfigurationList));

        return new DefaultPointcutAdvisor(pointcut, new EncryptMethodInterceptor(dataEncryptionService, encryptionConfigurationList, Collections.singletonList("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptionMethodInterceptorTestService.ignoredMethod")));
    }

``` 

Default implementation of `TextEncryptionService` is `BytesEncryptorTextEncryptService` and uses Springs BytesEncryptor that is passed in as argument
for text encryption. `BytesEncryptor` can be created with fixed password and salt or using random generated on each restart (in former case encrypted data should not be persisted).
It also accepts charset used for converting byte array to string and vice versa.

Default implementation of `DataEncryptionService` is `DefaultDataEncryptService` it is responsible for resolving data to be encrypted/decrypted from method arguments and return value and calling `TextEncryptionService` to do actual encryption/decryption operation.
To be able to use annotations on methods `EncryptDataAspect` bean is defined and to be able to resolve methods to encrypt/decypt from `EncryptionConfiguration` list an `Advisor` bean should be defined.
If encryption or decryption fails `EncryptionOperationFailedException` exception is thrown and original exception can be found in the cause (in default encryption service implementation `BytesEncryptorTextEncryptService`).


## Encrypt and Decrypt using annotations

Library can be used by using annotations:

- `EncryptResult`
- `DecryptArgument`

`EncryptResult` is used to encrypt result of method execution. Data that is encrypted is specified in
property named: `resultPathList`, it contains a list of paths to properties that should be encrypted.
Path list supports collections and plain properties also allowing data encryption for `CompletableFurure`, `Mono` and `Flux` instances. So for example if property inside list named `listOfObjects`
with name: `propertyToEncrypt` should be encrypted then  `resultPathList` should have the value of: `"listOfObjects.propertyToEncrypt"`.

`DecryptArgument` is used to decrypt method arguments that have been previously encrypted. Arguments that
are decrypted should have been encrypted with same service and password/salt combination that has been used for encryption.
Data that is decrypted is specified in property `argumentPathList`. Path list also supports collections and plain
properties. Syntax is same as for `EncryptResult` annotation.


## Encrypt and Decrypt using Spring configuration

Library can also be used by specifying fully qualified method names and additional arguments using `EncryptionConfiguration` list.

`EncryptionConfiguration` consists of the following data:

- `methodToEncryptDecrypt` - method that should be encrypted/decrypted containing fully qualified method name i.e. for class com.Example and method print it should be com.Example.print

- `propertyToEncryptDecryptList` - a list of property paths to encrypt or decrypt

- `encryptionOperation` - enum indicating encryption operation can be either `ENCRYPT` or `DECRYPT` 

example configuration is following:

``` 
final List<EncryptionConfiguration> encryptionConfigurationList = Arrays.asList(
    new EncryptionConfiguration("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptDataAspectTestService.dataToEncryptFromConfiguration", Collections.singletonList("value"), EncryptionOperation.ENCRYPT),
    new EncryptionConfiguration("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptionMethodInterceptorTestService.*", Collections.singletonList("value"), EncryptionOperation.ENCRYPT),
    new EncryptionConfiguration("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptionMethodInterceptorTestService.*", Collections.singletonList("value"), EncryptionOperation.DECRYPT)
);
```

This configuration performs encryption of `DefaultEncryptDataAspectTestService` method `dataToEncryptFromConfiguration` return value with key `value`
and also decrypts arguments for all method defined in `DefaultEncryptionMethodInterceptorTestService` and encrypts all values returned from methods with key `value`.

## Usage

For example if we want to encrypt/decrypt arguments of `testMethod` defined in service `net.croz.encypt.TestService` we can use following annotation configuration (requires `EncryptDataAspect` bean in context):

```

    @DecryptArgument(argumentPathList = "value")
    @EncryptResult(resultPathList = "value")
    ValueWithPropertyValue testMethod(ValueWithPropertyValue value)  


```

or we can use the following encrypt configuration:

```

    @Bean
    public Advisor encryptorAdvisor(final DataEncryptionService dataEncryptionService) {
        final List<EncryptionConfiguration> encryptionConfigurationList = Arrays.asList(
            new EncryptionConfiguration("net.croz.encypt.TestService.testMethod", Collections.singletonList("value"), EncryptionOperation.ENCRYPT),
            new EncryptionConfiguration("net.croz.encypt.TestService.testMethod", Collections.singletonList("value"), EncryptionOperation.DECRYPT)
        );

        final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        pointcut.setExpression(PointcutResolvingUtil.resolvePointcutFromEncryptionConfigurationList(encryptionConfigurationList));

        return new DefaultPointcutAdvisor(pointcut, new EncryptMethodInterceptor(dataEncryptionService, encryptionConfigurationList, Collections.singletonList("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptionMethodInterceptorTestService.ignoredMethod")));
    }


```
