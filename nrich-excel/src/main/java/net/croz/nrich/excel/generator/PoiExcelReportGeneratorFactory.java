/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

package net.croz.nrich.excel.generator;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.generator.ExcelReportGenerator;
import net.croz.nrich.excel.api.generator.ExcelReportGeneratorFactory;
import net.croz.nrich.excel.api.model.TypeDataFormat;
import net.croz.nrich.excel.api.request.CreateReportGeneratorRequest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.util.List;

@RequiredArgsConstructor
public class PoiExcelReportGeneratorFactory implements ExcelReportGeneratorFactory {

    private final ResourceLoader resourceLoader;

    private final List<CellValueConverter> cellValueConverterList;

    private final List<TypeDataFormat> typeDataFormatList;

    @Override
    public ExcelReportGenerator createReportGenerator(CreateReportGeneratorRequest request) {
        Assert.isTrue(request.getOutputStream() != null, "OutputStream cannot be null");
        Assert.hasText(request.getTemplatePath(), "Template path cannot be null");
        Assert.isTrue(request.getFirstRowIndex() >= 0, "Row index must be greater or equal to 0");

        InputStream template = resolveTemplate(request.getTemplatePath());

        return new PoiExcelReportGenerator(
            cellValueConverterList, request.getOutputStream(), template, request.getTemplateVariableList(), typeDataFormatList, request.getColumnDataFormatList(), request.getFirstRowIndex()
        );
    }

    @SneakyThrows
    private InputStream resolveTemplate(String templatePath) {
        return resourceLoader.getResource(templatePath).getInputStream();
    }
}
