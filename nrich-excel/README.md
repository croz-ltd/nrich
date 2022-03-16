# nrich-excel

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-excel/badge.svg?color=blue)](https://maven-badges.herokuapp.com/maven-central/net.croz.nrich/nrich-excel)

## Overview

nrich-excel is a library intended to simplify excel report generation for simple reports (i.e. exporting search results). It uses Apache POI library for excel creation. Excel reports are generated
from templates (standard excel files) and library also supports template variable resolving. The data is written to a provided OutputStream (users are expected to close it, library doesn't close it).

## Setting up Spring beans

To be able to use this library following configuration is required:

```
    @Bean
    public CellValueConverter defaultCellValueConverter() {
        return new DefaultCellValueConverter();
    }

    @Bean
    public ExcelReportGeneratorFactory excelReportGeneratorFactory(ResourceLoader resourceLoader, List<CellValueConverter> cellValueConverterList) {
        List<TypeDataFormat> typeDataFormatList = TypeDataFormatUtil.resolveTypeDataFormatList("dd.MM.yyyy.", "dd.MM.yyyy. HH:mm", "#,##0", "#,##0.00", true, Collections.singletonList(new TypeDataFormat(Date.clas, "dd-MM-yyyy"));

        return new PoiExcelReportGeneratorFactory(resourceLoader, cellValueConverterList, typeDataFormatList);
    }

    @Bean
    public ExcelReportService excelReportService(ExcelReportGeneratorFactory excelReportGeneratorFactory) {
        return new DefaultExcelReportService(excelReportGeneratorFactory);
    }

```

`CellValueConverter` is responsible for converting objects to values to be written in excel. Users can provided their own implementations and/or use
`DefaultCellValueConverter`.

`TypeDataFormat` is reponsible for resolving a list of `TypeDataFormat` instances that decide with what format a specific class will be written to excel. It accepts a list of formats for value
conversion (`dateFormat`, `dateTimeFormat`, `integerNumberFormat`, `decimalNumberFormat`), option should dates be written with time component or not
(`writeDateWithTime`) and a list of formats that will override defaults for specific class (for example if `Instant` should be written in different format than `Date`)

`ExcelReportGeneratorFactory` is responsible for creating and writing data to actual reports. Default implementation is `PoiExcelReportGeneratorFactory`
that uses Apache POI library for writing data.

`ExcelReportService` is the interface for users to use when creating reports.

## Usage

To be able to create reports users should inject `ExcelReportService` and call `void createExcelReport(CreateExcelReportRequest request)`
method.

Method accepts argument of type `CreateExcelReportRequest` when processing report `ExcelReportService` will then call `Object[][] resolveMultiRowData(int start, int limit)` method
from `MultiRowDataProvider` until it returns and empty result starting from zero and incrementing start by batch size.

`MultiRowDataProvider` is responsible for resolving data. It can do so by for example invoking directly repository methods. So for example a method that resolves data from a repository
named `ExampleRepository` for class `Example`:

```
@Setter
@Getter
public class Example {

    private String name;

    private Date date;
}

```

would look like this:

  ```
@RequiredArgsConstructor
public class ExampleRepositoryMultiRowDataProvider implements MultiRowDataProvider {

    private ExampleRepository exampleRepository;

    @Override
    public Object[][] resolveMultiRowData(int start, int limit) {
        Page<Example> exampleList = exampleRepository.findAll(PageableUtil.convertToPageable(start, limit));

        return exampleList.getContent().stream()
                .map(value -> new Object[] { value.getName(), value.getDate() })
                .toArray(Object[][]::new);
    }
}


```

Example usage of `ExcelReportService` is:

```
    // file where data will be written
    File file = new File("directory/excel-report.xlsx");
    // rows in excel
    Object[][] rowData = new Object[][] { { 1.1, "value 1", new Date(), new Date() }, { 2.2, "value 2", new Date(), new Date() };
    // no need for batching since we have only two records
    MultiRowDataProvider multiRowDataProvider = (start, limit) -> start == 0 ? rowData : null;

    // template variable defined in template with value ${templateVariable} will be replaced with resolvedValue
    List<TemplateVariable> templateVariableList = Collections.singletonList(new TemplateVariable("templateVariable", "resolvedValue"));

    // data format for columns 2 and 3 is overriden one date is written with dd-MM-yyyy format and another with dd-MM-yyyy HH:mm format
    List<ColumnDataFormat> columnDataFormatList = Arrays.asList(new ColumnDataFormat(2, "dd-MM-yyyy"), new ColumnDataFormat(3, "dd-MM-yyyy HH:mm"));

    try (FileOutputStream outputStream = new FileOutputStream(file)) {
        // first row index is 3 since first two rows contain column headers
        CreateExcelReportRequest request = CreateExcelReportRequest.builder().templateVariableList(templateVariableList).columnDataFormatList(columnDataFormatList).multiRowDataProvider(multiRowDataProvider).batchSize(10).outputStream(outputStream).templatePath("classpath:excel/template.xlsx").firstRowIndex(3).build();

        excelReportService.createExcelReport(request);
    }


```
