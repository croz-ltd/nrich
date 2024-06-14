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

import net.croz.nrich.excel.api.model.MultiRowDataProvider;
import net.croz.nrich.excel.api.request.CreateExcelReportRequest;

import java.io.OutputStream;

public final class ExcelReportRequestGeneratingUtil {

    private static final String TEMPLATE_PATH = "classpath:excel/template.xlsx";

    private ExcelReportRequestGeneratingUtil() {
    }

    public static CreateExcelReportRequest createExcelReportRequest(Object[][] rowData, int batchSize, OutputStream outputStream, int firstRowIndex) {
        return CreateExcelReportRequest.fromFlatData(rowData)
            .batchSize(batchSize)
            .outputStream(outputStream)
            .templatePath(TEMPLATE_PATH)
            .autoSizeColumns(true)
            .firstRowIndex(firstRowIndex).build();
    }

    public static CreateExcelReportRequest createExcelReportRequest(MultiRowDataProvider multiRowDataProvider, int batchSize, OutputStream outputStream, int firstRowIndex) {
        return CreateExcelReportRequest.fromRowDataProvider(multiRowDataProvider)
            .batchSize(batchSize)
            .outputStream(outputStream)
            .templatePath(TEMPLATE_PATH)
            .firstRowIndex(firstRowIndex).build();
    }
}
