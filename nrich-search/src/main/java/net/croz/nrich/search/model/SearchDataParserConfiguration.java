package net.croz.nrich.search.model;

import lombok.Builder;
import lombok.Getter;
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.model.property.SearchPropertyConfiguration;
import net.croz.nrich.search.api.model.operator.SearchOperatorOverride;
import net.croz.nrich.search.api.model.property.SearchPropertyMapping;

import java.util.List;

@Getter
@Builder
public class SearchDataParserConfiguration {

    private final boolean resolvePropertyMappingUsingPrefix;

    private final List<SearchPropertyMapping> propertyMappingList;

    private final List<SearchOperatorOverride> searchOperatorOverrideList;

    private final SearchPropertyConfiguration searchPropertyConfiguration;

    public static SearchDataParserConfiguration fromSearchConfiguration(final SearchConfiguration<?, ?, ?> searchConfiguration) {
        return new SearchDataParserConfiguration(searchConfiguration.isResolvePropertyMappingUsingPrefix(), searchConfiguration.getPropertyMappingList(), searchConfiguration.getSearchOperatorOverrideList(), searchConfiguration.getSearchPropertyConfiguration());
    }

}
