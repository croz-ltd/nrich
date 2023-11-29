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

package net.croz.nrich.excel.generator;

import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.model.ColumnDataFormat;
import net.croz.nrich.excel.api.model.TemplateVariable;
import net.croz.nrich.excel.api.model.TypeDataFormat;
import net.croz.nrich.excel.converter.DefaultCellValueConverter;
import net.croz.nrich.excel.stub.TestEnum;
import net.croz.nrich.excel.util.TypeDataFormatUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.createWorkbookAndResolveSheet;
import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.getCellValue;
import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.getRowCellStyleList;
import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.getRowCellValueList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PoiExcelReportGeneratorTest {

    private static final int TEMPLATE_DATA_FIRST_ROW_INDEX = 3;

    private PoiExcelReportGenerator excelReportGenerator;

    private ByteArrayOutputStream outputStream;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @BeforeEach
    void setup() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");

        List<CellValueConverter> cellValueConverterList = Collections.singletonList(new DefaultCellValueConverter(messageSource));
        InputStream template = this.getClass().getResourceAsStream("/excel/template.xlsx");
        List<TemplateVariable> templateVariableList = Collections.singletonList(new TemplateVariable("templateVariable", "resolvedValue"));
        List<ColumnDataFormat> columnDataFormatList = Arrays.asList(new ColumnDataFormat(2, "dd-MM-yyyy"), new ColumnDataFormat(3, "dd-MM-yyyy HH:mm"));
        List<TypeDataFormat> additionalFormatList = Collections.singletonList(new TypeDataFormat(Date.class, "dd-MM-yyyy"));
        List<TypeDataFormat> typeDataFormatList = TypeDataFormatUtil.resolveTypeDataFormatList(
            "dd.MM.yyyy.", "dd.MM.yyyy. HH:mm", "#,##0", "#,##0.00", true, additionalFormatList
        );

        outputStream = new ByteArrayOutputStream();

        excelReportGenerator = new PoiExcelReportGenerator(
            cellValueConverterList, outputStream, template, templateVariableList, typeDataFormatList, columnDataFormatList, TEMPLATE_DATA_FIRST_ROW_INDEX
        );
    }

    @Test
    void shouldExportDataToExcel() {
        // given
        Instant now = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Object[] rowData = new Object[] {
            1.1, "value", new Date(now.toEpochMilli()), ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS), OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS), now, now, 1, 1.5F, (short) 1,
            LocalDate.now(), LocalDateTime.now().truncatedTo(ChronoUnit.DAYS), BigDecimal.valueOf(1.5), 10L, TestEnum.FIRST, TestEnum.SECOND, null
        };
        // when resolving data from cells all dates are converted to instant, all decimal numbers are converted to double and all whole numbers are converted to integer
        Object[] expectedRowData = new Object[] { 1.1, "value", now, now, now, now, now, 1, 1.5, 1, now, now, 1.5, 10, "First", "SECOND", null };

        // when
        excelReportGenerator.writeRowData(rowData);
        excelReportGenerator.flush();

        // and when
        Sheet sheet = createWorkbookAndResolveSheet(outputStream);

        // then
        assertThat(sheet).isNotNull();

        assertThat(sheet.getRow(0).getCell(0)).isNotNull();
        assertThat(getCellValue(sheet.getRow(0).getCell(0))).isEqualTo("resolvedValue");
        assertThat(getRowCellValueList(sheet.getRow(TEMPLATE_DATA_FIRST_ROW_INDEX))).containsExactly(expectedRowData);
    }

    @Test
    void shouldThrowExceptionWhenTryingToWriteToClosedGenerator() {
        // given
        Object[] rowData = new Object[] { 1.0, "value", Instant.now().truncatedTo(ChronoUnit.DAYS), Instant.now().truncatedTo(ChronoUnit.DAYS) };

        excelReportGenerator.writeRowData(rowData);
        excelReportGenerator.flush();

        // when
        Throwable thrown = catchThrowable(() -> excelReportGenerator.writeRowData(rowData));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Template has been closed and cannot be written anymore");
    }

    @Test
    void shouldSetDefaultFormatToColumnsWithoutDefinedFormat() {
        // given
        Instant now = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Object[] rowData = new Object[] { 1.1, 1, now, now, (short) 1, LocalDate.now(), LocalDateTime.now().truncatedTo(ChronoUnit.DAYS), BigDecimal.valueOf(1.5), 10L, new Date() };

        // when
        excelReportGenerator.writeRowData(rowData);
        excelReportGenerator.flush();

        // and when
        Sheet sheet = createWorkbookAndResolveSheet(outputStream);

        // then
        assertThat(getRowCellStyleList(sheet.getRow(TEMPLATE_DATA_FIRST_ROW_INDEX))).containsExactly(
            "#,##0.00", "#,##0", "dd-MM-yyyy", "dd-MM-yyyy HH:mm", "#,##0", "dd.MM.yyyy.", "dd.MM.yyyy. HH:mm", "#,##0.00", "#,##0", "dd-MM-yyyy"
        );
    }
}
