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

package net.croz.nrich.excel.model;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.excel.api.model.CellHolder;
import org.apache.poi.ss.usermodel.Cell;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@RequiredArgsConstructor
public class PoiCellHolder implements CellHolder {

    private final Cell cell;

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
        if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        }
        else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        }
        else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        }
        else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
        }
        else if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
        }
        else if (value instanceof String) {
            cell.setCellValue(value.toString());
        }
        else {
            throw new IllegalArgumentException("Set cell value called with unrecognized type!");
        }
    }
}
