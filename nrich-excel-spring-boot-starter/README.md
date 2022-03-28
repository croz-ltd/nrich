# nrich-excel-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-excel-spring-boot-starter/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-excel-spring-boot-starter)

## Overview

Spring Boot starter for `nrich-excel` module. The purpose of `nrich-excel` is to provide an easy way of generating simple excel reports (i.e. for search results). Starter module provides
a `@Configuration` class (`NrichExcelAutoConfiguration`) with default configuration of `nrich-excel` module (while allowing for overriding with conditional annotations) and `@ConfigurationProperties`
class (`NrichExcelProperties`) with default configured values and does automatic registration through `spring.factories`.

## Usage

### Adding the Dependency

The artifact is published on [Maven Central Repository](https://search.maven.org/). To include the dependency use the following configurations.

With Maven:

```xml

<dependency>
    <groupId>net.croz.nrich</groupId>
    <artifactId>nrich-excel-spring-boot-starter</artifactId>
    <version>${nrich.version}</version>
</dependency>

```

With Gradle:

```groovy

implementation "net.croz.nrich:nrich-excel-spring-boot-starter:${nrich.version}"

```

Note if using `nrich-bom` dependency versions should be omitted.

### Configuration

Configuration is done through a property file, available properties and descriptions are given bellow (all properties are prefixed with nrich.excel which is omitted for readability):

| property                  | description                                                                                               | default value     |
|---------------------------|-----------------------------------------------------------------------------------------------------------|-------------------|
| date-format               | Date format used to set excel cell style for date values                                                  | dd.MM.yyyy.       |
| date-time-format          | Date time format used to set excel cell style for date time values                                        | dd.MM.yyyy. HH:mm |
| write-date-with-time      | Whether dateFormat or dateTimeFormat should be used for date time values                                  | false             |
| integer-number-format     | Integer number format used to set excel cell style for integer numbers (short, integer, long, BigInteger) | #,##0             |
| decimal-number-format     | Decimal number format used to set excel cell style for decimal numbers (float, double, BigDecimal)        | #,##0.00          |
| type-data-format-list     | A list of formats that overrides default formats for classes.                                             |                   |
| default-converter-enabled | Whether default converter  should be enabled (unless users provide their own it should be enabled)        | true              |

The default configuration values in yaml format for easier modification are given bellow:

```yaml

nrich.excel:
  date-format: dd.MM.yyyy.
  date-time-format: dd.MM.yyyy. HH:mm
  write-date-with-time: false
  integer-number-format: #,##0
  decimal-number-format: #,##0.00
  type-data-format-list:
  default-converter-enabled: true

```

### Using the module

After adding the dependency and adjusting the properties if necessary a bean of type `ExcelExportService` is available for dependency injection. An example usage is given bellow (more detailed
examples are found in `nrich-excel` [README.MD](../nrich-excel/README.md)):

```java

@RequiredArgsConstructor
@Service
public class ExcelReportServiceExampleService {

    private final ExcelReportService excelReportService;

    public File createExcelReport(Object[][] data) {
        MultiRowDataProvider multiRowDataProvider = (start, limit) -> start == 0 ? data : null;
        File file = new File("excel-directory", "export.xlxs");

        CreateExcelReportRequest request = CreateExcelReportRequest.builder()
            .multiRowDataProvider(multiRowDataProvider)
            .batchSize(data.length)
            .outputFile(file)
            .templatePath("classpath:excel/template.xlsx")
            .firstRowIndex(1).build();

        return excelReportService.createExcelReport(request);
    }
}

```
