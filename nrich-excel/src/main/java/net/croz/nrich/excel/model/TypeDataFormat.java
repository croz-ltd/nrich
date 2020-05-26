package net.croz.nrich.excel.model;

import lombok.Data;

@Data
public class TypeDataFormat {

    private final Class<?> type;

    private final String dataFormat;

}
