package net.croz.nrich.excel.api.model;

public interface CellHolder {

    int getColumnIndex();

    int getRowIndex();

    void setCellValue(Object value);

}
