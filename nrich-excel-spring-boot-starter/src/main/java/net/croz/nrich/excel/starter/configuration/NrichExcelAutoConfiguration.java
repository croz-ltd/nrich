package net.croz.nrich.excel.starter.configuration;

import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.generator.ExcelReportGeneratorFactory;
import net.croz.nrich.excel.api.model.TypeDataFormat;
import net.croz.nrich.excel.api.service.ExcelReportService;
import net.croz.nrich.excel.converter.DefaultCellValueConverter;
import net.croz.nrich.excel.generator.PoiExcelReportGeneratorFactory;
import net.croz.nrich.excel.service.DefaultExcelReportService;
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
    public ExcelReportGeneratorFactory excelReportGeneratorFactory(ResourceLoader resourceLoader, List<CellValueConverter> cellValueConverterList, NrichExcelProperties excelProperties) {
        List<TypeDataFormat> typeDataFormatList = TypeDataFormatUtil.resolveTypeDataFormatList(
            excelProperties.getDateFormat(), excelProperties.getDateTimeFormat(), excelProperties.getIntegerNumberFormat(),
            excelProperties.getDecimalNumberFormat(), excelProperties.isWriteDateWithTime(), excelProperties.getTypeDataFormatList()
        );

        return new PoiExcelReportGeneratorFactory(resourceLoader, cellValueConverterList, typeDataFormatList);
    }

    @ConditionalOnMissingBean
    @Bean
    public ExcelReportService excelReportService(ExcelReportGeneratorFactory excelReportGeneratorFactory) {
        return new DefaultExcelReportService(excelReportGeneratorFactory);
    }
}
