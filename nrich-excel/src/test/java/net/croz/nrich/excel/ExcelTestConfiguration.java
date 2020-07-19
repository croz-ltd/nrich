package net.croz.nrich.excel;

import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.generator.ExcelExportGeneratorFactory;
import net.croz.nrich.excel.api.model.TypeDataFormat;
import net.croz.nrich.excel.api.service.ExcelExportService;
import net.croz.nrich.excel.converter.DefaultCellValueConverter;
import net.croz.nrich.excel.generator.PoiExcelExportGeneratorFactory;
import net.croz.nrich.excel.service.DefaultExcelExportService;
import net.croz.nrich.excel.util.TypeDataFormatUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class ExcelTestConfiguration {

    @Bean
    public CellValueConverter defaultCellValueConverter() {
        return new DefaultCellValueConverter();
    }

    @Bean
    public ExcelExportGeneratorFactory excelExportGeneratorFactory(final ResourceLoader resourceLoader, final List<CellValueConverter> cellValueConverterList) {
        final List<TypeDataFormat> typeDataFormatList = TypeDataFormatUtil.resolveTypeDataFormatList("dd.MM.yyyy.", "dd.MM.yyyy. HH:mm", "#,##0", "#,##0.00", true, null);

        return new PoiExcelExportGeneratorFactory(resourceLoader, cellValueConverterList, typeDataFormatList);
    }

    @Bean
    public ExcelExportService excelExportService(final ExcelExportGeneratorFactory excelExportGeneratorFactory) {
        return new DefaultExcelExportService(excelExportGeneratorFactory);
    }
}
