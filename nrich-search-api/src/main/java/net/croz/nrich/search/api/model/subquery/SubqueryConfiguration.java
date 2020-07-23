package net.croz.nrich.search.api.model.subquery;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.property.SearchPropertyJoin;

/**
 * Configuration for subquery. Allows specifying custom root entity, joins and resolving property values from
 * search request either by property prefix or by a separate class holding all subquery restrictions.
 */
@Setter
@Getter
@Builder
public class SubqueryConfiguration {

    /**
     * Subquery root entity.
     */
    private Class<?> rootEntity;

    /**
     * Properties by which join between entities should be performed.
     */
    private SearchPropertyJoin joinBy;

    /**
     * Prefix that will be used to resolve subquery restrictions from search request.
     */
    private String propertyPrefix;

    /**
     * Name of property that holds all subquery restrictions.
     */
    private String restrictionPropertyHolder;

}
