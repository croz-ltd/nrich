package net.croz.nrich.excel.service;

import net.croz.nrich.excel.request.CreateExcelReportRequest;

import java.io.File;

public interface ExcelExportService {

    File createExcelReport(CreateExcelReportRequest request);

}
