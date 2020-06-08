package net.croz.nrich.excel.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ColumnDataFormat {

    private final int columnIndex;

    private final String dataFormat;

}