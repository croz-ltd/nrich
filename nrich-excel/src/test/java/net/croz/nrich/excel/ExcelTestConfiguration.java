package net.croz.nrich.excel;

import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.generator.ExcelReportGeneratorFactory;
import net.croz.nrich.excel.api.model.TypeDataFormat;
import net.croz.nrich.excel.api.service.ExcelReportService;
import net.croz.nrich.excel.converter.DefaultCellValueConverter;
import net.croz.nrich.excel.generator.PoiExcelReportGeneratorFactory;
import net.croz.nrich.excel.service.DefaultExcelReportService;
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
    public ExcelReportGeneratorFactory excelReportGeneratorFactory(ResourceLoader resourceLoader, List<CellValueConverter> cellValueConverterList) {
        List<TypeDataFormat> typeDataFormatList = TypeDataFormatUtil.resolveTypeDataFormatList("dd.MM.yyyy.", "dd.MM.yyyy. HH:mm", "#,##0", "#,##0.00", true, null);

        return new PoiExcelReportGeneratorFactory(resourceLoader, cellValueConverterList, typeDataFormatList);
    }

    @Bean
    public ExcelReportService excelReportService(ExcelReportGeneratorFactory excelReportGeneratorFactory) {
        return new DefaultExcelReportService(excelReportGeneratorFactory);
    }
}
