package net.croz.nrich.excel.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.excel.api.generator.ExcelExportGenerator;
import net.croz.nrich.excel.api.generator.ExcelExportGeneratorFactory;
import net.croz.nrich.excel.api.model.MultiRowDataProvider;
import net.croz.nrich.excel.api.request.CreateExcelReportRequest;
import net.croz.nrich.excel.api.request.CreateReportGeneratorRequest;
import net.croz.nrich.excel.api.service.ExcelExportService;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public class DefaultExcelExportService implements ExcelExportService {

    private final ExcelExportGeneratorFactory excelExportGeneratorFactory;

    @Override
    public File createExcelReport(final CreateExcelReportRequest request) {

        Assert.notNull(request.getMultiRowDataProvider(), "Row data provider cannot be null!");
        Assert.isTrue(request.getBatchSize() > 0, "Batch size must be greater than zero!");

        final CreateReportGeneratorRequest createReportGeneratorRequest = toCreateReportGeneratorRequest(request);
        final ExcelExportGenerator excelExportGenerator = excelExportGeneratorFactory.createReportGenerator(createReportGeneratorRequest);

        final MultiRowDataProvider multiRowDataProvider = request.getMultiRowDataProvider();
        final int limit = request.getBatchSize();

        int start = 0;
        Object[][] rowBatchData;
        while ((rowBatchData = multiRowDataProvider.resolveMultiRowData(start, limit)) != null) {

            Arrays.stream(rowBatchData)
                    .filter(Objects::nonNull)
                    .forEach(excelExportGenerator::writeRowData);

            start += limit;
        }

        excelExportGenerator.flushAndClose();

        return request.getOutputFile();
    }

    private CreateReportGeneratorRequest toCreateReportGeneratorRequest(final CreateExcelReportRequest reportRequest) {
        return CreateReportGeneratorRequest.builder()
                .columnDataFormatList(reportRequest.getColumnDataFormatList())
                .firstRowIndex(reportRequest.getFirstRowIndex())
                .outputFile(reportRequest.getOutputFile())
                .templatePath(reportRequest.getTemplatePath())
                .templateVariableList(reportRequest.getTemplateVariableList())
                .build();
    }
}
