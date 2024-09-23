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

package net.croz.nrich.excel.service;

import net.croz.nrich.excel.ExcelTestConfiguration;
import net.croz.nrich.excel.api.model.MultiRowDataProvider;
import net.croz.nrich.excel.api.request.CreateExcelReportRequest;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.ByteArrayOutputStream;

import static net.croz.nrich.excel.testutil.ExcelReportRequestGeneratingUtil.createExcelReportRequest;
import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.createWorkbookAndResolveSheet;
import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.getRowCellValueList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(ExcelTestConfiguration.class)
class DefaultExcelReportServiceTest {

    private static final int TEMPLATE_DATA_FIRST_ROW_INDEX = 3;

    private static final Object[][] DEFAULT_ROW_DATA = { { 1.1, "value" } };

    @Autowired
    private DefaultExcelReportService excelReportService;

    @Test
    void shouldCreateExcelReport() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CreateExcelReportRequest request = createExcelReportRequest(DEFAULT_ROW_DATA, 10, outputStream, TEMPLATE_DATA_FIRST_ROW_INDEX);

        // when
        excelReportService.createExcelReport(request);

        // then
        Sheet sheet = createWorkbookAndResolveSheet(outputStream);

        // then
        assertThat(sheet).isNotNull();

        assertThat(sheet.getRow(0).getCell(0)).isNotNull();
        assertThat(getRowCellValueList(sheet.getRow(TEMPLATE_DATA_FIRST_ROW_INDEX))).containsExactly(DEFAULT_ROW_DATA[0]);
    }

    @Test
    void shouldThrowExceptionOnInvalidBatchSize() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CreateExcelReportRequest request = createExcelReportRequest(DEFAULT_ROW_DATA, -1, outputStream, TEMPLATE_DATA_FIRST_ROW_INDEX);

        // when
        Throwable thrown = catchThrowable(() -> excelReportService.createExcelReport(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Batch size must be greater than zero!");
    }

    @Test
    void shouldThrowExceptionOnMissingRowDataProvider() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CreateExcelReportRequest request = createExcelReportRequest((MultiRowDataProvider) null, 10, outputStream, TEMPLATE_DATA_FIRST_ROW_INDEX);

        // when
        Throwable thrown = catchThrowable(() -> excelReportService.createExcelReport(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Row data provider cannot be null!");
    }

    @Test
    void shouldStopReadingWhenRowProviderReturnsEmptyArray() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MultiRowDataProvider multiRowDataProvider = (start, limit) -> start == 0 ? DEFAULT_ROW_DATA : new Object[0][0];
        CreateExcelReportRequest request = createExcelReportRequest(multiRowDataProvider, 10, outputStream, TEMPLATE_DATA_FIRST_ROW_INDEX);

        // when
        excelReportService.createExcelReport(request);

        // then
        Sheet sheet = createWorkbookAndResolveSheet(outputStream);

        // then
        assertThat(sheet).isNotNull();
        assertThat(getRowCellValueList(sheet.getRow(TEMPLATE_DATA_FIRST_ROW_INDEX))).containsExactly(DEFAULT_ROW_DATA[0]);
    }
}
