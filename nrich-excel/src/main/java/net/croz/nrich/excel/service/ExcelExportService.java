package net.croz.nrich.excel.service;

import net.croz.nrich.excel.request.CreateReportGeneratorRequest;

import java.io.File;

public interface ExcelExportService {

    File createExcelReport(CreateReportGeneratorRequest request);

}
