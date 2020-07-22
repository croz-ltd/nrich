package net.croz.nrich.excel.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Holds data format for excel column. Used when overriding default data format for specific column.
 */
@RequiredArgsConstructor
@Getter
public class ColumnDataFormat {

    /**
     * Column index.
     */
    private final int columnIndex;

    /**
     * Column data format (i.e dd.MM.yyyy, #,##0.00 etc.).
     */
    private final String dataFormat;

}
