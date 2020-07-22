package net.croz.nrich.excel.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Holds data format for specific type.
 */
@RequiredArgsConstructor
@Getter
public class TypeDataFormat {

    /**
     * Type for which formatting is required (i.e. {@link java.util.Date}, {@link Float} etc).
     */
    private final Class<?> type;

    /**
     * Type data format data format (i.e dd.MM.yyyy, #,##0.00 etc).
     */
    private final String dataFormat;

}
