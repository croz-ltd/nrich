package net.croz.nrich.search.model;

import lombok.Value;
import net.croz.nrich.search.api.model.SearchOperator;

@Value
public class Restriction {

    // TODO every aspect of path should have a marker if it is plural
    String path;

    SearchOperator searchOperator;

    Object value;

    boolean isPluralAttribute;

}
