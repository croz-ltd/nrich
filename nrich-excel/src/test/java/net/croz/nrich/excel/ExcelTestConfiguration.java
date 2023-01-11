/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
