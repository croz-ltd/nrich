package net.croz.nrich.excel.converter;

import org.apache.poi.ss.usermodel.Cell;

public interface CellValueConverter {

    void setCellValue(Cell cell, Object value);

    String getDataFormat();

    boolean supports(Cell cell, Object value);
}
