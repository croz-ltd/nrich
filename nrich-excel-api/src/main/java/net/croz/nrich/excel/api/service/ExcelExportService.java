package net.croz.nrich.excel.api.service;

import net.croz.nrich.excel.api.request.CreateExcelReportRequest;

import java.io.File;

public interface ExcelExportService {

    File createExcelReport(CreateExcelReportRequest request);

}
