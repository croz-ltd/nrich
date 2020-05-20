package net.croz.nrich.search.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SearchDataParserConfiguration {

    private boolean resolveFieldMappingUsingPrefix;

    private List<SearchPropertyMapping> propertyMappingList;

    private List<SearchOperatorOverride> searchOperatorOverrideList;

    private SearchFieldConfiguration searchFieldConfiguration;

    public static SearchDataParserConfiguration fromSearchConfiguration(final SearchConfiguration<?, ?, ?> searchConfiguration) {
        return new SearchDataParserConfiguration(searchConfiguration.isResolveFieldMappingUsingPrefix(), searchConfiguration.getPropertyMappingList(), searchConfiguration.getSearchOperatorOverrideList(), searchConfiguration.getSearchFieldConfiguration());
    }

}
