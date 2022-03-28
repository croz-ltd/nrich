package net.croz.nrich.excel.testutil;

import net.croz.nrich.excel.api.model.MultiRowDataProvider;
import net.croz.nrich.excel.api.request.CreateExcelReportRequest;

import java.io.OutputStream;

public final class ExcelReportRequestGeneratingUtil {

    private static final String TEMPLATE_PATH = "classpath:excel/template.xlsx";

    private ExcelReportRequestGeneratingUtil() {
    }

    public static CreateExcelReportRequest createExcelReportRequest(Object[][] rowData, int batchSize, OutputStream outputStream, int firstRowIndex) {
        return CreateExcelReportRequest.fromFlatData(rowData)
            .batchSize(batchSize)
            .outputStream(outputStream)
            .templatePath(TEMPLATE_PATH)
            .firstRowIndex(firstRowIndex).build();
    }

    public static CreateExcelReportRequest createExcelReportRequest(MultiRowDataProvider multiRowDataProvider, int batchSize, OutputStream outputStream, int firstRowIndex) {
        return CreateExcelReportRequest.fromRowDataProvider(multiRowDataProvider)
            .batchSize(batchSize)
            .outputStream(outputStream)
            .templatePath(TEMPLATE_PATH)
            .firstRowIndex(firstRowIndex).build();
    }
}
