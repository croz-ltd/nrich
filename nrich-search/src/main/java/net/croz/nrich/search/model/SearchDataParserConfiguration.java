package net.croz.nrich.search.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SearchDataParserConfiguration {

    private final boolean resolveFieldMappingUsingPrefix;

    private final List<SearchPropertyMapping> propertyMappingList;

    private final List<SearchOperatorOverride> searchOperatorOverrideList;

    private final SearchFieldConfiguration searchFieldConfiguration;

    public static SearchDataParserConfiguration fromSearchConfiguration(final SearchSpecification<?, ?, ?> searchSpecification) {
        return new SearchDataParserConfiguration(searchSpecification.isResolveFieldMappingUsingPrefix(), searchSpecification.getPropertyMappingList(), searchSpecification.getSearchOperatorOverrideList(), searchSpecification.getSearchFieldConfiguration());
    }

}
