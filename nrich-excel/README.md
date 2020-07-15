# nrich-excel

## Overview
nrich-excel is a library intended to simplify excel report generation for simple reports (i.e. exporting search results).
It uses Apache POI library for excel creation. Excel reports are generated from templates and library also supports
template variable processing.

## Setting up Spring beans

To be able to use this library following configuration is required:

```
    @Bean
    public CellValueConverter defaultCellValueConverter() {
        return new DefaultCellValueConverter("dd.MM.yyyy", "dd.MM.yyyy. HH:mm", "#,##0", "#,##0.00", true);
    }

    @Bean
    public ExcelExportGeneratorFactory excelExportGeneratorFactory(final ResourceLoader resourceLoader, final List<CellValueConverter> cellValueConverterList) {
        return new PoiExcelExportGeneratorFactory(resourceLoader, cellValueConverterList);
    }

    @Bean
    public ExcelExportService excelExportService(final ExcelExportGeneratorFactory excelExportGeneratorFactory) {
        return new DefaultExcelExportService(excelExportGeneratorFactory);
    }

```


`CellValueConverter` is responsible for converting objects to values to be written in excel. Users can provided their own implementations and/or use
`DefaultCellValueConverter`. 

`DefaultCellValueConverter` accepts a list of formats for value conversion (`dateFormat`, `dateTimeFormat`, `integerNumberFormat`, `decimalNumberFormat`) and option should dates be written with time component or not 
(`writeDateWithTime`)

`ExcelExportGeneratorFactory` is responsible for creating and writing data to actual reports. Default implementation is `PoiExcelExportGeneratorFactory`
that uses Apache POI library for writing data.

`ExcelExportService` is the interface for users to use when creating reports. 


## Usage

To be able to create reports users should inject `ExcelExportService` and call `File createExcelReport(CreateExcelReportRequest request)`
method.

Method accepts argument of type `CreateExcelReportRequest` that contains following properties:

- `File outputFile`

  File where report will be written to.

- `String templatePath`

  Path to template (template is resolved from this path using Springs `ResourceLoader`).

- `List<TemplateVariable> templateVariableList`

  A list of `TemplateVariable` instances consisting of `name` and `value` that will be used to fill variables defined in the template.
  Variables are defined in template with following synstax: `${varaibleName}` 

- `List<ColumnDataFormat> columnDataFormatList`

  A list of `ColumnDataFormat` instances consisting of `columnIndex` and `dataFormat` that allow for overriding of data format for specific columns. 

- `int firstRowIndex`

  Row index from which data should be written to report.

- `int batchSize`

  Batch size used for data fetch from `MultiRowDataProvider`. 

- `MultiRowDataProvider multiRowDataProvider`

  Interface that is used for fetching data, it should return multiple row data
  
  ```
    @FunctionalInterface
    public interface MultiRowDataProvider {

        Object[][] resolveMultiRowData(int start, int limit);

    }
  
  ```
  
  `ExcelExportService` will then call `Object[][] resolveMultiRowData(int start, int limit)` method until it return 
  empty result starting from zero and incrementing start by batch size.
  

Example usage of `ExcelExportService` is:

```


    final File file = new File("director/excel-report.xlsx");
    // rows in excel
    final Object[][] rowData = new Object[][] { { 1.1, "value 1" }, { 2.2, "value 2 };
    // no need for batching since we have only two records
    final MultiRowDataProvider multiRowDataProvider = (start, limit) -> start == 0 ? rowData : null;

    // first row index is 3 since first two rows contain column headers
    final CreateExcelReportRequest request = CreateExcelReportRequest.builder().multiRowDataProvider(multiRowDataProvider).batchSize(10).outputFile(file).templatePath("classpath:excel/template.xlsx").firstRowIndex(3).build();
   
    final File createdFile = excelExportService.createExcelReport(request);

```
