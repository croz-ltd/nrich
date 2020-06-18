package net.croz.nrich.excel.api.generator;

import net.croz.nrich.excel.api.request.CreateReportGeneratorRequest;

public interface ExcelExportGeneratorFactory {

    ExcelExportGenerator createReportGenerator(CreateReportGeneratorRequest request);

}
