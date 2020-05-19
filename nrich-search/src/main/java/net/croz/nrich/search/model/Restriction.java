package net.croz.nrich.search.model;

import lombok.Data;

@Data
public class Restriction {

    // TODO every aspect of path should have a marker if it is plural
    private final String path;

    private final SearchOperator searchOperator;

    private final Object value;

    private final boolean isPluralAttribute;

}
