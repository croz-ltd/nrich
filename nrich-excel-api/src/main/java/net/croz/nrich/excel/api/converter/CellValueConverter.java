package net.croz.nrich.excel.api.converter;

import net.croz.nrich.excel.api.model.TypeDataFormat;
import net.croz.nrich.excel.api.model.CellHolder;

import java.util.List;

public interface CellValueConverter {

    void setCellValue(CellHolder cell, Object value);

    boolean supports(CellHolder cell, Object value);

    List<TypeDataFormat> typeDataFormatList();
}
