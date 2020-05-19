package net.croz.nrich.search.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

// T -> root persistent entity, P -> projection class, R -> search request instance
@Builder
@Getter
@Setter
public class SearchConfiguration<T, P, R> {

    private Function<R, Class<T>> rootEntityResolver;

    private List<SearchJoin<R>> joinList;

    private List<SearchProjection<R>> projectionList;

    private Class<P> resultClass;

    private boolean resolveFieldMappingUsingPrefix;

    private List<SearchPropertyMapping> propertyMappingList;

    private Map<String, SearchOperator> pathSearchOperatorMap;

    private Map<Class<?>, SearchOperator> typeSearchOperatorMap;

    // TODO enable restriction type by association name
    private PluralAssociationRestrictionType pluralAssociationRestrictionType;

    private List<SubqueryConfiguration> subqueryConfigurationList;

}
