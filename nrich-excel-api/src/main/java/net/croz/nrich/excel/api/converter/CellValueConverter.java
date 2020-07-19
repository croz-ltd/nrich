package net.croz.nrich.excel.api.converter;

import net.croz.nrich.excel.api.model.CellHolder;

public interface CellValueConverter {

    void setCellValue(CellHolder cell, Object value);

    boolean supports(CellHolder cell, Object value);

}
