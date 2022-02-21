package net.croz.nrich.excel.api.converter;

import net.croz.nrich.excel.api.model.CellHolder;

/**
 * Optionally converts and set values on {@link CellHolder} instance.
 */
public interface CellValueConverter {

    /**
     * Set cell value, perform conversion if necessary
     *
     * @param cell  cell to set value on
     * @param value value to set
     */
    void setCellValue(CellHolder cell, Object value);

    /**
     * Returns true if this converter supports cell and value combination
     *
     * @param cell  cell that value will be set
     * @param value value to be set
     * @return whether cell and value combination is supported
     */
    boolean supports(CellHolder cell, Object value);

}
