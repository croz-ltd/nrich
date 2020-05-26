package net.croz.nrich.excel.service.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.excel.factory.ExcelExportGeneratorFactory;
import net.croz.nrich.excel.generator.ExcelExportGenerator;
import net.croz.nrich.excel.model.RowDataProvider;
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
    public File createExcelReport(final CreateReportGeneratorRequest request) {

        Assert.notNull(request.getRowDataProvider(), "Row data provider cannot be null!");
        Assert.isTrue(request.getBatchSize() > 0, "Batch size must be greater than zero!");

        final ExcelExportGenerator excelExportGenerator = excelExportGeneratorFactory.createReportGenerator(request);

        final RowDataProvider rowDataProvider = request.getRowDataProvider();
        int limit = request.getBatchSize();
        int start = 0;

        Object[][] rowBatchData;
        while ((rowBatchData = rowDataProvider.resolveRowData(start, limit)) != null) {

            Arrays.stream(rowBatchData)
                    .filter(Objects::nonNull)
                    .forEach(excelExportGenerator::writeRowData);

            start += limit;
        }

        excelExportGenerator.flushAndClose();

        return request.getOutputFile();
    }
}
