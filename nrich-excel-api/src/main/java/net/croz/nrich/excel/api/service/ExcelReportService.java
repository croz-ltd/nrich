package net.croz.nrich.excel.api.service;

import net.croz.nrich.excel.api.request.CreateExcelReportRequest;

import java.io.File;

/**
 * Creates and writes excel report to a file.
 */
public interface ExcelReportService {

    /**
     * Returns file with excel report data written to it.
     *
     * @param request configuration options for excel report with data to be written
     * @return file with excel report
     */
    File createExcelReport(CreateExcelReportRequest request);

}
