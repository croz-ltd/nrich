package net.croz.nrich.registry.core.service;

import lombok.RequiredArgsConstructor;
import lombok.Value;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EntityManagerRegistryEntityFinderService implements RegistryEntityFinderService {

    private final EntityManager entityManager;

    private final ModelMapper modelMapper;

    private final Map<Class<?>, ManagedTypeWrapper> managedTypeWrapperMap = new ConcurrentHashMap<>();

    @Override
    public <T> T findEntityInstance(final Class<T> type, final Object id) {
        final QueryCondition queryCondition = queryWherePartWithParameterMap(type, id, false);

        final String joinFetchQueryPart = managedTypeWrapperMap.get(type).getSingularAssociationList().stream()
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
        return queryWherePartWithParameterMap(type, id, true).parameterMap;
    }

    private <T> QueryCondition queryWherePartWithParameterMap(final Class<T> type, final Object id, final boolean rawParameterValue) {
        final ManagedTypeWrapper managedTypeWrapper = managedTypeWrapper(type);

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

                wherePartList.add(toParameterExpression(key, rawParameterValue));

                parameterMap.put(toParameterVariable(key, rawParameterValue), convertedIdValue);
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

            wherePartList.add(toParameterExpression(idAttributeName, rawParameterValue));

            parameterMap.put(toParameterVariable(idAttributeName, rawParameterValue), convertedIdValue);
        }

        return new QueryCondition(String.join(RegistryDataConstants.FIND_QUERY_SEPARATOR, wherePartList), parameterMap);
    }

    private String toParameterExpression(final String key, final boolean rawParameterValue) {
        return String.format(RegistryDataConstants.QUERY_PARAMETER_FORMAT, key, toParameterVariable(key, rawParameterValue));
    }

    private String toParameterVariable(final String key, final boolean rawParameterValue) {
        if (rawParameterValue) {
            return key;
        }

        final String[] keyList = key.split("\\.");

        return Arrays.stream(keyList)
                .map(StringUtils::capitalize)
                .collect(Collectors.joining());
    }

    private ManagedTypeWrapper managedTypeWrapper(final Class<?> type) {
        if (!managedTypeWrapperMap.containsKey(type)) {
            managedTypeWrapperMap.put(type, new ManagedTypeWrapper(entityManager.getMetamodel().managedType(type)));
        }

        return managedTypeWrapperMap.get(type);
    }

    @Value
    private static class QueryCondition {

        String wherePart;

        Map<String, Object> parameterMap;

    }
}
