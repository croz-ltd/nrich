package net.croz.nrich.excel;

import net.croz.nrich.excel.converter.CellValueConverter;
import net.croz.nrich.excel.converter.impl.DefaultCellValueConverter;
import net.croz.nrich.excel.factory.ExcelExportGeneratorFactory;
import net.croz.nrich.excel.api.service.ExcelExportService;
import net.croz.nrich.excel.service.impl.ExcelExportServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class ExcelTestConfiguration {

    @Bean
    public DefaultCellValueConverter defaultCellValueConverter() {
        return new DefaultCellValueConverter("dd.MM.yyyy", "dd.MM.yyyy. HH:mm", "#,##0", "#,##0.00", true);
    }

    @Bean
    public ExcelExportGeneratorFactory excelExportGeneratorFactory(final ResourceLoader resourceLoader, final List<CellValueConverter> cellValueConverterList) {
        return new ExcelExportGeneratorFactory(resourceLoader, cellValueConverterList);
    }

    @Bean
    public ExcelExportService excelExportService(final ExcelExportGeneratorFactory excelExportGeneratorFactory) {
        return new ExcelExportServiceImpl(excelExportGeneratorFactory);
    }
}
