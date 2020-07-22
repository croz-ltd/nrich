package net.croz.nrich.excel.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Holder for variable that will be replaced in excel report template .
 * Variables are defined in template in following form <pre>${variableName}</pre>
 */
@RequiredArgsConstructor
@Getter
public class TemplateVariable {

    /**
     * Name of template variable to replace.
     */
    private final String name;

    /**
     * Value of template variable that will replace name.
     */
    private final String value;

}
