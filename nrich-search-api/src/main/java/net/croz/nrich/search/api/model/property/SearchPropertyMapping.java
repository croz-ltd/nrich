package net.croz.nrich.search.api.model.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Mapping of request property to entity property.
 */
@RequiredArgsConstructor
@Getter
public class SearchPropertyMapping {

    /**
     * Name of property on search request class.
     */
    private final String name;

    /**
     * Path on entity for specified property.
     */
    private final String path;

}
