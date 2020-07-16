package net.croz.nrich.excel.api.converter;

import net.croz.nrich.excel.api.model.TypeDataFormat;
import net.croz.nrich.excel.api.model.CellHolder;

import java.util.List;

public interface CellValueConverter {

    void setCellValue(CellHolder cell, Object value);

    boolean supports(CellHolder cell, Object value);

    // TODO this doesn't seem to belong here, move to separate class
    List<TypeDataFormat> typeDataFormatList();
}
