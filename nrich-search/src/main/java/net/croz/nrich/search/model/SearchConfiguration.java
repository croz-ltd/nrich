package net.croz.nrich.search.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.request.SearchRequest;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Builder
@Getter
@Setter
public class SearchConfiguration<T, S extends SearchRequest<T, S>> {

    private Function<S, Class<T>> rootEntityResolver;

    private List<SearchJoin<S>> joinList;

    private List<SearchProjection<S>> projectionList;

    private Class<?> resultClass;

    private boolean resolveFieldMappingUsingPrefix;

    private List<SearchPropertyMapping> propertyMappingList;

    private Map<String, SearchOperator> pathSearchOperatorMap;

    private Map<Class<?>, SearchOperator> typeSearchOperatorMap;

    // TODO enable restriction type by association name
    private PluralAssociationRestrictionType pluralAssociationRestrictionType;

    private List<SubqueryConfiguration> subqueryConfigurationList;

}
