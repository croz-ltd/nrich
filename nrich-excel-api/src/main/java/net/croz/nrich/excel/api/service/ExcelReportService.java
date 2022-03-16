package net.croz.nrich.excel.api.service;

import net.croz.nrich.excel.api.request.CreateExcelReportRequest;

/**
 * Creates and writes excel report to the provided OutputStream.
 */
public interface ExcelReportService {

    /**
     * Writes the excel report to  the provided OutputStream.
     *
     * @param request configuration options for excel report with data to be written
     */
    void createExcelReport(CreateExcelReportRequest request);

}
