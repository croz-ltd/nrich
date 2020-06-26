package net.croz.nrich.search.api.model.operator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SearchOperatorOverride {

    private final Class<?> propertyType;

    private final String propertyPath;

    private final SearchOperator searchOperator;

    public static SearchOperatorOverride forType(final Class<?> propertyType, final SearchOperator searchOperator) {
        return new SearchOperatorOverride(propertyType, null, searchOperator);
    }

    public static SearchOperatorOverride forPath(final String propertyPath, final SearchOperator searchOperator) {
        return new SearchOperatorOverride(null, propertyPath, searchOperator);
    }
}
