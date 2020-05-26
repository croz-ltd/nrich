package net.croz.nrich.excel.converter;

import net.croz.nrich.excel.model.TypeDataFormat;
import org.apache.poi.ss.usermodel.Cell;

import java.util.List;

public interface CellValueConverter {

    void setCellValue(Cell cell, Object value);

    boolean supports(Cell cell, Object value);

    List<TypeDataFormat> typeDataFormatList();
}
