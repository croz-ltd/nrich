package net.croz.nrich.excel.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.excel.api.generator.ExcelReportGenerator;
import net.croz.nrich.excel.api.generator.ExcelReportGeneratorFactory;
import net.croz.nrich.excel.api.model.MultiRowDataProvider;
import net.croz.nrich.excel.api.request.CreateExcelReportRequest;
import net.croz.nrich.excel.api.request.CreateReportGeneratorRequest;
import net.croz.nrich.excel.api.service.ExcelReportService;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public class DefaultExcelReportService implements ExcelReportService {

    private final ExcelReportGeneratorFactory excelReportGeneratorFactory;

    @Override
    public File createExcelReport(CreateExcelReportRequest request) {
        Assert.notNull(request.getMultiRowDataProvider(), "Row data provider cannot be null!");
        Assert.isTrue(request.getBatchSize() > 0, "Batch size must be greater than zero!");

        CreateReportGeneratorRequest createReportGeneratorRequest = toCreateReportGeneratorRequest(request);
        ExcelReportGenerator excelReportGenerator = excelReportGeneratorFactory.createReportGenerator(createReportGeneratorRequest);

        MultiRowDataProvider multiRowDataProvider = request.getMultiRowDataProvider();

        int limit = request.getBatchSize();
        int start = 0;
        Object[][] rowBatchData;
        while ((rowBatchData = multiRowDataProvider.resolveMultiRowData(start, limit)) != null) {

            if (rowBatchData.length == 0) {
                break;
            }

            Arrays.stream(rowBatchData)
                    .filter(Objects::nonNull)
                    .forEach(excelReportGenerator::writeRowData);

            start += limit;
        }

        excelReportGenerator.flushAndClose();

        return request.getOutputFile();
    }

    private CreateReportGeneratorRequest toCreateReportGeneratorRequest(CreateExcelReportRequest reportRequest) {
        return CreateReportGeneratorRequest.builder()
                .columnDataFormatList(reportRequest.getColumnDataFormatList())
                .firstRowIndex(reportRequest.getFirstRowIndex())
                .outputFile(reportRequest.getOutputFile())
                .templatePath(reportRequest.getTemplatePath())
                .templateVariableList(reportRequest.getTemplateVariableList())
                .build();
    }
}
