package net.croz.nrich.registry.api.data.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Registry entity search parameters.
 */
@Setter
@Getter
public class SearchParameter {

    /**
     * List of properties to search.
     */
    private List<String> propertyNameList;

    /**
     * Search query.
     */
    private String query;

}
