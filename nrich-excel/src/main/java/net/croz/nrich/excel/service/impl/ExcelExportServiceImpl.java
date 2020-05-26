package net.croz.nrich.excel.service.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.excel.factory.ExcelExportGeneratorFactory;
import net.croz.nrich.excel.generator.ExcelExportGenerator;
import net.croz.nrich.excel.model.MultiRowDataProvider;
import net.croz.nrich.excel.request.CreateExcelReportRequest;
import net.croz.nrich.excel.request.CreateReportGeneratorRequest;
import net.croz.nrich.excel.service.ExcelExportService;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public class ExcelExportServiceImpl implements ExcelExportService {

    private final ExcelExportGeneratorFactory excelExportGeneratorFactory;

    @Override
    public File createExcelReport(final CreateExcelReportRequest request) {

        Assert.notNull(request.getMultiRowDataProvider(), "Row data provider cannot be null!");
        Assert.isTrue(request.getBatchSize() > 0, "Batch size must be greater than zero!");

        final CreateReportGeneratorRequest createReportGeneratorRequest = toCreateReportGeneratorRequest(request);
        final ExcelExportGenerator excelExportGenerator = excelExportGeneratorFactory.createReportGenerator(createReportGeneratorRequest);

        final MultiRowDataProvider multiRowDataProvider = request.getMultiRowDataProvider();
        int limit = request.getBatchSize();
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
