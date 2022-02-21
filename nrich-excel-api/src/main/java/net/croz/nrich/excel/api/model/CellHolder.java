package net.croz.nrich.excel.api.model;

/**
 * Represents a single cell in excel report.
 */
public interface CellHolder {

    /**
     * Column index (zero based)
     *
     * @return column index
     */
    int getColumnIndex();

    /**
     * Row index (zero based)
     *
     * @return row index
     */
    int getRowIndex();

    /**
     * Set value to cell
     *
     * @param value value to set
     */
    void setCellValue(Object value);

}
