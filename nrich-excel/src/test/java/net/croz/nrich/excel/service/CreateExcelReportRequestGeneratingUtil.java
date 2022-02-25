package net.croz.nrich.excel.service;

import net.croz.nrich.excel.api.model.MultiRowDataProvider;
import net.croz.nrich.excel.api.request.CreateExcelReportRequest;

import java.io.File;

public final class CreateExcelReportRequestGeneratingUtil {

    private CreateExcelReportRequestGeneratingUtil() {
    }

    public static CreateExcelReportRequest createExcelReportRequest(Object[][] rowData, int batchSize, File file, int firstRowIndex) {
        MultiRowDataProvider multiRowDataProvider = (start, limit) -> start == 0 ? rowData : null;

        return createExcelReportRequest(rowData == null ? null : multiRowDataProvider, batchSize, file, firstRowIndex);
    }

    public static CreateExcelReportRequest createExcelReportRequest(MultiRowDataProvider multiRowDataProvider, int batchSize, File file, int firstRowIndex) {


        return CreateExcelReportRequest.builder()
                .multiRowDataProvider(multiRowDataProvider)
                .batchSize(batchSize)
                .outputFile(file)
                .templatePath("classpath:excel/template.xlsx")
                .firstRowIndex(firstRowIndex).build();
    }
}
