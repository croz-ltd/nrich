package net.croz.nrich.excel.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TemplateVariable {

    private final String name;

    private final String value;

}
