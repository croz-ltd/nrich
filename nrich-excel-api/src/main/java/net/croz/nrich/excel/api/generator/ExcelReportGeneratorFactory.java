package net.croz.nrich.excel.api.generator;

import net.croz.nrich.excel.api.request.CreateReportGeneratorRequest;

/**
 * Creates instances of {@link ExcelReportGenerator} that are used for writing to excel report.
 */
public interface ExcelReportGeneratorFactory {

    /**
     * Returns configured {@link ExcelReportGenerator} instance for specified request parameters.
     *
     * @param request containing configuration options for  {@link ExcelReportGenerator} instance.
     * @return configured {@link ExcelReportGenerator} instance
     */
    ExcelReportGenerator createReportGenerator(CreateReportGeneratorRequest request);

}
