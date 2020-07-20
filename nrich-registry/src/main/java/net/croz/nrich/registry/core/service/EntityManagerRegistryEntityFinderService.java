package net.croz.nrich.registry.core.service;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.croz.nrich.registry.api.core.service.RegistryEntityFinderService;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import org.modelmapper.ModelMapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EntityManagerRegistryEntityFinderService implements RegistryEntityFinderService {

    private final EntityManager entityManager;

    private final ModelMapper modelMapper;

    private final Map<String, ManagedTypeWrapper> classNameManagedTypeWrapperMap;

    @Override
    public <T> T findEntityInstance(final Class<T> type, final Object id) {
        final QueryCondition queryCondition = queryWherePartWithParameterMap(type, id, true);

        final String joinFetchQueryPart = classNameManagedTypeWrapperMap.get(type.getName()).getSingularAssociationList().stream()
                .map(attribute -> String.format(RegistryDataConstants.FIND_QUERY_JOIN_FETCH, attribute.getName())).collect(Collectors.joining(" "));

        final String entityWithAlias = String.format(RegistryDataConstants.PROPERTY_SPACE_FORMAT, type.getName(), RegistryDataConstants.ENTITY_ALIAS);

        final String querySelectPart = String.format(RegistryDataConstants.PROPERTY_SPACE_FORMAT, entityWithAlias, joinFetchQueryPart.trim());

        final String fullQuery = String.format(RegistryDataConstants.FIND_QUERY, querySelectPart, queryCondition.wherePart);

        @SuppressWarnings("unchecked")
        final TypedQuery<T> query = (TypedQuery<T>) entityManager.createQuery(fullQuery);

        queryCondition.parameterMap.forEach(query::setParameter);

        return query.getSingleResult();
    }

    @Override
    public <T> Map<String, Object> resolveIdParameterMap(final Class<T> type, final Object id) {
        return queryWherePartWithParameterMap(type, id, false).parameterMap;
    }

    private <T> QueryCondition queryWherePartWithParameterMap(final Class<T> type, final Object id, final boolean convertParameterToQueryFormat) {
        final ManagedTypeWrapper managedTypeWrapper = classNameManagedTypeWrapperMap.get(type.getName());

        final List<String> wherePartList = new ArrayList<>();
        final Map<String, Object> parameterMap = new HashMap<>();

        if (managedTypeWrapper.isIdClassIdentifier()) {
            Assert.isTrue(id instanceof Map, "Id should be instance of Map for @IdClass identifier");

            @SuppressWarnings("unchecked")
            final Map<String, Object> idMap = ((Map<Object, Object>) id).entrySet().stream()
                    .collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));

            final Map<String, Class<?>> idClassPropertyMap = managedTypeWrapper.getIdClassPropertyMap();

            idClassPropertyMap.forEach((key, value) -> {
                final Object convertedIdValue = modelMapper.map(idMap.get(key), value);

                wherePartList.add(toParameterExpression(key, convertParameterToQueryFormat));

                parameterMap.put(toParameterVariable(key, convertParameterToQueryFormat), convertedIdValue);
            });
        }
        else {
            final Object convertedIdValue;
            if (managedTypeWrapper.isEmbeddedIdentifier()) {
                Assert.isTrue(id instanceof Map || managedTypeWrapper.getEmbeddableIdType().getJavaType().equals(id.getClass()), "Id should be instance of Map or EmbeddedId for @EmbeddedId identifier");

                convertedIdValue = modelMapper.map(id, managedTypeWrapper.getEmbeddableIdType().getJavaType());
            }
            else {
                convertedIdValue = modelMapper.map(id, managedTypeWrapper.getIdentifiableType().getIdType().getJavaType());
            }

            final String idAttributeName = managedTypeWrapper.getIdAttributeName();

            wherePartList.add(toParameterExpression(idAttributeName, convertParameterToQueryFormat));

            parameterMap.put(toParameterVariable(idAttributeName, convertParameterToQueryFormat), convertedIdValue);
        }

        return new QueryCondition(String.join(RegistryDataConstants.FIND_QUERY_SEPARATOR, wherePartList), parameterMap);
    }

    private String toParameterExpression(final String key, final boolean convertParameterToQueryFormat) {
        return String.format(RegistryDataConstants.QUERY_PARAMETER_FORMAT, key, toParameterVariable(key, convertParameterToQueryFormat));
    }

    private String toParameterVariable(final String key, final boolean convertParameterToQueryFormat) {
        if (!convertParameterToQueryFormat) {
            return key;
        }

        final String[] keyList = key.split("\\.");

        return Arrays.stream(keyList)
                .map(StringUtils::capitalize)
                .collect(Collectors.joining());
    }

    @Value
    private static class QueryCondition {

        String wherePart;

        Map<String, Object> parameterMap;

    }
}
