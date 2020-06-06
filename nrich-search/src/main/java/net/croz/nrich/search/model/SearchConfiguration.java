package net.croz.nrich.search.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

// TODO allow for easier initialization of search configuration and sync all classes (static vs builder etc)
// T -> root persistent entity, P -> projection class (can be same as root), R -> search request instance
@Setter
@Getter
@Builder
public class SearchConfiguration<T, P, R> {

    private Function<R, Class<T>> rootEntityResolver;

    private List<SearchJoin<R>> joinList;

    private List<SearchProjection<R>> projectionList;

    private Class<P> resultClass;

    private boolean resolveFieldMappingUsingPrefix;

    private List<SearchPropertyMapping> propertyMappingList;

    private List<SearchOperatorOverride> searchOperatorOverrideList;

    // TODO enable restriction type by association name
    private PluralAssociationRestrictionType pluralAssociationRestrictionType;

    private List<SubqueryConfiguration> subqueryConfigurationList;

    private List<AdditionalRestrictionResolver<T, P, R>> additionalRestrictionResolverList;

    private boolean distinct;

    private boolean anyMatch;

    @Builder.Default
    private SearchFieldConfiguration searchFieldConfiguration = SearchFieldConfiguration.defaultSearchFieldConfiguration();

    public static <T, P, R> SearchConfiguration<T, P, R> emptyConfiguration() {
        return SearchConfiguration.<T, P, R>builder().searchFieldConfiguration(SearchFieldConfiguration.defaultSearchFieldConfiguration()).build();
    }

    public static <T, P, R> SearchConfiguration<T, P, R> emptyConfigurationMatchingAny() {
        return SearchConfiguration.<T, P, R>builder().searchFieldConfiguration(SearchFieldConfiguration.defaultSearchFieldConfiguration()).anyMatch(true).build();
    }

    public static <T, P, R> SearchConfiguration<T, P, R> emptyConfigurationWithDefaultMappingResolve() {
        return SearchConfiguration.<T, P, R>builder().searchFieldConfiguration(SearchFieldConfiguration.defaultSearchFieldConfiguration()).resolveFieldMappingUsingPrefix(true).build();
    }
}
