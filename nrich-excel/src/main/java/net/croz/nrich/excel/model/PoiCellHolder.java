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

package net.croz.nrich.excel.model;

import net.croz.nrich.excel.api.model.CellHolder;
import org.apache.poi.ss.usermodel.Cell;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public record PoiCellHolder(Cell cell) implements CellHolder {

    @Override
    public int getColumnIndex() {
        return cell.getColumnIndex();
    }

    @Override
    public int getRowIndex() {
        return cell.getRowIndex();
    }

    @Override
    public void setCellValue(Object value) {
        if (value instanceof Boolean booleanValue) {
            cell.setCellValue(booleanValue);
        }
        else if (value instanceof Number numberValue) {
            cell.setCellValue(numberValue.doubleValue());
        }
        else if (value instanceof Date dateValue) {
            cell.setCellValue(dateValue);
        }
        else if (value instanceof Calendar calendarValue) {
            cell.setCellValue(calendarValue);
        }
        else if (value instanceof LocalDateTime localDateTimeValue) {
            cell.setCellValue(localDateTimeValue);
        }
        else if (value instanceof LocalDate localDateValue) {
            cell.setCellValue(localDateValue);
        }
        else if (value instanceof String stringValue) {
            cell.setCellValue(stringValue);
        }
        else {
            throw new IllegalArgumentException("Set cell value called with unrecognized type!");
        }
    }
}
