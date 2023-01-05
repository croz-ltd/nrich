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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public final class PoiDataResolverUtil {

    private PoiDataResolverUtil() {
    }

    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue().toInstant() : asIntegerIfApplicable(cell.getNumericCellValue());
            case BOOLEAN:
                return cell.getBooleanCellValue();
            default:
                return null;
        }
    }

    public static List<Object> getRowCellValueList(Row row) {
        List<Object> resultList = new ArrayList<>();

        row.forEach(cell -> resultList.add(getCellValue(cell)));

        return resultList;
    }

    private static Object asIntegerIfApplicable(double value) {
        int intValue = Double.valueOf(value).intValue();

        if (intValue == value) {
            return intValue;
        }

        return value;
    }

    public static List<String> getRowCellStyleList(Row row) {
        List<String> resultList = new ArrayList<>();

        row.forEach(cell -> resultList.add(cell.getCellStyle().getDataFormatString()));

        return resultList;
    }

    @SneakyThrows
    public static Sheet createWorkbookAndResolveSheet(ByteArrayOutputStream outputStream) {
        return new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray())).getSheetAt(0);
    }
}
