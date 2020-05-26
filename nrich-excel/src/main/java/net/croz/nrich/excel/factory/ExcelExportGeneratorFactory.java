package net.croz.nrich.excel.factory;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.croz.nrich.excel.converter.CellValueConverter;
import net.croz.nrich.excel.generator.DefaultExcelExportGenerator;
import net.croz.nrich.excel.generator.ExcelExportGenerator;
import net.croz.nrich.excel.request.CreateReportGeneratorRequest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.util.List;

@RequiredArgsConstructor
public class ExcelExportGeneratorFactory {

    private final ResourceLoader resourceLoader;

    private final List<CellValueConverter> cellValueConverterList;

    public ExcelExportGenerator createReportGenerator(final CreateReportGeneratorRequest request) {
        Assert.isTrue(request.getOutputFile() != null && request.getOutputFile().exists(), "Output file cannot be null");
        Assert.hasText(request.getTemplatePath(), "Template path cannot be null");
        Assert.isTrue(request.getFirstRowIndex() >= 0, "Row index must be greater or equal to 0");

        final InputStream template = resolveTemplate(request.getTemplatePath());

        return new DefaultExcelExportGenerator(cellValueConverterList, request.getOutputFile(), template, request.getTemplateVariableList(), request.getColumnDataFormatList(), request.getFirstRowIndex());
    }

    @SneakyThrows
    private InputStream resolveTemplate(final String templatePath) {
        return resourceLoader.getResource(templatePath).getInputStream();
    }
}
