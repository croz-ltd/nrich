# nrich-encrypt-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-encrypt-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-encrypt-spring-boot-starter)

## Overview

Spring Boot starter for `nrich-encrypt` module. The purpose of `nrich-encrypt` is to provide encryption and decryption of sensitive data (i.e. for sending data to client, storing in database etc.).
Starter module provides a `@Configuration` class (`NrichEncryptAutoConfiguration`) with default configuration of `nrich-encrypt` module (while allowing for overriding with conditional annotations),
and a `@ConfigurationProperties` class (`NrichEncryptProperties`) with default configured values and does automatic registration through `spring.factories`.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository](https://search.maven.org/). To include the dependency use the following configurations.

With Maven:

```xml

<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-encypt-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>

```

With Gradle:

```groovy

implementation "net.croz.nrich:nrich-encrypt-spring-boot-starter:${nrich.version}"

```

Note if using `nrich-bom` dependency versions should be omitted.

### Configuration

Configuration is done through a property file, available properties and descriptions are given bellow (all properties are prefixed with nrich.encrypt which is omitted for readability):

| property                      | description                                                                                                                                                                     | default value |
|-------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| encryption-configuration-list | Configuration list containing methods for encryption and decryption                                                                                                             |               |
| ignored-method-list           | Used in conjunction with encryption-configuration-list. It allows defining methods that will not be encrypted. Methods should be in format: fullyQualifiedClasName.methodName.  |               |
| text-encrypt-charset          | Charset to use for encryption                                                                                                                                                   | UTF-8         |     |
| encrypt-aspect-enabled        | Whether an aspect bean that handles encryption for `@EncryptResult` and `@DecryptArgument` annotations is active                                                                | true          |
| encrypt-advisor-enabled       | Whether an advisor bean that handles encryption from encryption-configuration-list is active                                                                                    | true          |
| encrypt-password              | Encrypt password if null data is encrypted with randomly generated password on each application restart. If encrypted data will be persisted this parameter should be specified |               |
| encrypt-salt                  | Encrypt salt if null data is encrypted with randomly generated password on each application restart. If encrypted data will be persisted this parameter should be specified     |               |

The default configuration values in yaml format for easier modification are given bellow:

```yaml

nrich.encrypt:
  encryption-configuration-list:
  ignored-method-list:
  text-encrypt-charset: UTF-8
  encrypt-aspect-enabled: true
  encrypt-advisor-enabled: true
  encrypt-password:
  encrypt-salt:

```

### Using the module

There are two ways of using the module, one is defining all the methods for encryption/decryption inside a properties file and the other is using the annotations.

#### Using property file

An example configuration for encryption/decryption with predefined password and salt (this is just an example if not required they should not be specified) is given bellow. For
service `DocumentService`, property `documentId` of data returned from method `fetchDocuments` is encrypted and the same property passed in as parameter of method `updateDocument` is decrypted (it is
also possible to encrypt/decrypt all methods using `*` in that case ignored-method-list provides an easy way of excluding some methods):

```yaml

nrich.encrypt:
  encryption-configuration-list:
    - method-to-encrypt-decrypt: net.croz.example.DocumentService.fetchDocuments
      property-to-encrypt-decrypt-list: documentId
      encryption-operation: ENCRYPT
    - method-to-encrypt-decrypt: net.croz.example.DocumentService.updateDocument
      property-to-encrypt-decrypt-list: documentId
      encryption-operation: DECRYPT
  encrypt-password: 43a62546066cebe4
  encrypt-salt: 529564719fc0edc4

```

#### Using annotations

Alternative to property configuration is annotation usage, in that case methods that require encryption/decryption are annotated with `@EncryptResult` and `@DecryptArgument` annotations. Example is
given bellow (both annotations can be used on the same method also):

```java

@Service
public class DocumentService {

    @EncryptResult(resultPathList = "documentId")
    public List<Document> fetchDocument() {
        // code for fetching documents
    }

    public void updateDocument(@DecryptArgument(argumentPathList = "documentId") Document document) {
        // code for updating document
    }
}

```
