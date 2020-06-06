package net.croz.nrich.excel.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TypeDataFormat {

    private final Class<?> type;

    private final String dataFormat;

}
