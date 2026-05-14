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

package net.croz.nrich.excel.testutil;

import lombok.SneakyThrows;
import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.model.ColumnDataFormat;
import net.croz.nrich.excel.api.model.TemplateVariable;
import net.croz.nrich.excel.api.model.TypeDataFormat;
import net.croz.nrich.excel.converter.DefaultCellValueConverter;
import net.croz.nrich.excel.generator.PoiExcelReportGenerator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

public final class PoiExcelTemplateGeneratingUtil {

    private PoiExcelTemplateGeneratingUtil() {
    }

    @SneakyThrows
    public static byte[] createTemplateWithNumericAndStringCell() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(0);

            row.createCell(0).setCellValue(42);
            row.createCell(1).setCellValue("${templateVariable}");
            workbook.write(out);

            return out.toByteArray();
        }
    }

    public static PoiExcelReportGenerator createGenerator(OutputStream outputStream, InputStream template, List<TemplateVariable> templateVariableList, int startIndex) {
        return createGenerator(outputStream, template, templateVariableList, Collections.emptyList(), Collections.emptyList(), startIndex);
    }

    public static PoiExcelReportGenerator createGenerator(OutputStream outputStream, InputStream template, List<TemplateVariable> templateVariableList,
                                                          List<TypeDataFormat> typeDataFormatList, List<ColumnDataFormat> columnDataFormatList, int startIndex) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");

        List<CellValueConverter> cellValueConverterList = List.of(new DefaultCellValueConverter(messageSource));

        return new PoiExcelReportGenerator(
            cellValueConverterList, outputStream, template, templateVariableList, typeDataFormatList, columnDataFormatList, startIndex, false
        );
    }
}
