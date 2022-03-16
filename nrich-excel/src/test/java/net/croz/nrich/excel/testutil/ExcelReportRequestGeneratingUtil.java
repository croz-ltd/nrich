package net.croz.nrich.excel.testutil;

import lombok.SneakyThrows;
import net.croz.nrich.excel.api.model.MultiRowDataProvider;
import net.croz.nrich.excel.api.request.CreateExcelReportRequest;

import java.io.OutputStream;

public final class ExcelReportRequestGeneratingUtil {

    private ExcelReportRequestGeneratingUtil() {
    }

    public static CreateExcelReportRequest createExcelReportRequest(Object[][] rowData, int batchSize, OutputStream outputStream, int firstRowIndex) {
        MultiRowDataProvider multiRowDataProvider = (start, limit) -> start == 0 ? rowData : null;

        return createExcelReportRequest(rowData == null ? null : multiRowDataProvider, batchSize, outputStream, firstRowIndex);
    }

    @SneakyThrows
    public static CreateExcelReportRequest createExcelReportRequest(MultiRowDataProvider multiRowDataProvider, int batchSize, OutputStream outputStream, int firstRowIndex) {
        return CreateExcelReportRequest.builder()
            .multiRowDataProvider(multiRowDataProvider)
            .batchSize(batchSize)
            .outputStream(outputStream)
            .templatePath("classpath:excel/template.xlsx")
            .firstRowIndex(firstRowIndex).build();
    }
}
