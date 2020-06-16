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
    public void setCellValue(final Object value) {
        if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        if (value instanceof Number) {
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
