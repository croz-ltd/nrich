package net.croz.nrich.search.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SearchDataParserConfiguration {

    private boolean resolveFieldMappingUsingPrefix;

    private List<SearchPropertyMapping> propertyMappingList;

    private Map<String, SearchOperator> pathSearchOperatorMap;

    private Map<Class<?>, SearchOperator> typeSearchOperatorMap;

    public static SearchDataParserConfiguration fromSearchConfiguration(final SearchConfiguration<?, ?, ?> searchConfiguration) {
        return new SearchDataParserConfiguration(searchConfiguration.isResolveFieldMappingUsingPrefix(), searchConfiguration.getPropertyMappingList(), searchConfiguration.getPathSearchOperatorMap(), searchConfiguration.getTypeSearchOperatorMap());
    }

}
