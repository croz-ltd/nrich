package net.croz.nrich.excel.starter.configuration;

import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.generator.ExcelExportGeneratorFactory;
import net.croz.nrich.excel.api.model.TypeDataFormat;
import net.croz.nrich.excel.api.service.ExcelExportService;
import net.croz.nrich.excel.converter.DefaultCellValueConverter;
import net.croz.nrich.excel.generator.PoiExcelExportGeneratorFactory;
import net.croz.nrich.excel.service.DefaultExcelExportService;
import net.croz.nrich.excel.starter.properties.NrichExcelProperties;
import net.croz.nrich.excel.util.TypeDataFormatUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

@EnableConfigurationProperties(NrichExcelProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichExcelAutoConfiguration {

    @ConditionalOnProperty(name = "nrich.excel.default-converter-enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public CellValueConverter defaultCellValueConverter() {
        return new DefaultCellValueConverter();
    }

    @ConditionalOnMissingBean
    @Bean
    public ExcelExportGeneratorFactory excelExportGeneratorFactory(final ResourceLoader resourceLoader, final List<CellValueConverter> cellValueConverterList, final NrichExcelProperties excelProperties) {
        final List<TypeDataFormat> typeDataFormatList = TypeDataFormatUtil.resolveTypeDataFormatList(excelProperties.getDateFormat(), excelProperties.getDateTimeFormat(), excelProperties.getIntegerNumberFormat(), excelProperties.getDecimalNumberFormat(), excelProperties.isWriteDateWithTime(), excelProperties.getTypeDataFormatList());

        return new PoiExcelExportGeneratorFactory(resourceLoader, cellValueConverterList, typeDataFormatList);
    }

    @ConditionalOnMissingBean
    @Bean
    public ExcelExportService excelExportService(final ExcelExportGeneratorFactory excelExportGeneratorFactory) {
        return new DefaultExcelExportService(excelExportGeneratorFactory);
    }
}
