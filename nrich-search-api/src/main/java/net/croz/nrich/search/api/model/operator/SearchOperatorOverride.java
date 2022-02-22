package net.croz.nrich.search.api.model.operator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Allows for specifying override of default search operator. Search operators can be overridden for type or for property path.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SearchOperatorOverride {

    /**
     * Type for which to override search operator.
     */
    private final Class<?> propertyType;

    /**
     * Path for which to override search operator.
     */
    private final String propertyPath;

    /**
     * Overridden search operator.
     */
    private final SearchOperator searchOperator;

    public static SearchOperatorOverride forType(Class<?> propertyType, SearchOperator searchOperator) {
        return new SearchOperatorOverride(propertyType, null, searchOperator);
    }

    public static SearchOperatorOverride forPath(String propertyPath, SearchOperator searchOperator) {
        return new SearchOperatorOverride(null, propertyPath, searchOperator);
    }
}
