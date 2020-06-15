package net.croz.nrich.excel.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ColumnDataFormat {

    private final int columnIndex;

    private final String dataFormat;

}
