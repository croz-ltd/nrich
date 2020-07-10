package net.croz.nrich.registry.core.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.core.constants.RegistryCoreConstants;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.search.bean.MapSupportingDirectFieldAccessFallbackBeanWrapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EntityManagerRegistryEntityFinderService implements RegistryEntityFinderService {

    private final EntityManager entityManager;

    private final Map<Class<?>, ManagedTypeWrapper> managedTypeWrapperMap = new ConcurrentHashMap<>();

    @Override
    public <T> T findEntityInstance(final Class<T> type, final Object id) {
        Assert.isTrue(id instanceof Map || id instanceof Number, String.format("Id: %s is of not supported type!", id));

        final String wherePart;
        final Map<String, Object> parameterMap = new HashMap<>();
        if (id instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> idMap = ((Map<Object, Object>) id).entrySet().stream()
                    .collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));

            wherePart = idMap.entrySet().stream()
                    .map(entry -> toParameterExpression(entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(RegistryDataConstants.FIND_QUERY_SEPARATOR));

            idMap.forEach((key, value) -> parameterMap.put(toParameterVariable(key), resolveIdValue(value)));
        }
        else {
            wherePart = String.format(RegistryDataConstants.QUERY_PARAMETER_FORMAT, RegistryCoreConstants.ID_ATTRIBUTE, RegistryCoreConstants.ID_ATTRIBUTE);

            parameterMap.put(RegistryCoreConstants.ID_ATTRIBUTE, Long.valueOf(id.toString()));
        }

        final String joinFetchQueryPart = managedTypeWrapper(type).getAssociationList().stream()
                .map(attribute -> String.format(RegistryDataConstants.FIND_QUERY_JOIN_FETCH, attribute.getName())).collect(Collectors.joining(" "));

        final String entityWithAlias = String.format(RegistryDataConstants.PROPERTY_SPACE_FORMAT, type.getName(), RegistryDataConstants.ENTITY_ALIAS);

        final String querySelectPart = String.format(RegistryDataConstants.PROPERTY_SPACE_FORMAT, entityWithAlias, joinFetchQueryPart.trim());

        final String fullQuery = String.format(RegistryDataConstants.FIND_QUERY, querySelectPart, wherePart);

        @SuppressWarnings("unchecked")
        final TypedQuery<T> query = (TypedQuery<T>) entityManager.createQuery(fullQuery);

        parameterMap.forEach(query::setParameter);

        return query.getSingleResult();
    }

    private String toParameterExpression(final String key, final Object value) {
        final String keyWithId;
        if (value instanceof Number) {
            keyWithId = key;
        }
        else {
            keyWithId = String.format(RegistryDataConstants.PROPERTY_PREFIX_FORMAT, key, RegistryCoreConstants.ID_ATTRIBUTE);
        }

        return String.format(RegistryDataConstants.QUERY_PARAMETER_FORMAT, keyWithId, toParameterVariable(key));
    }

    private String toParameterVariable(final String key) {
        final String[] keyList = key.split("\\.");

        return Arrays.stream(keyList).map(StringUtils::capitalize).collect(Collectors.joining());
    }

    private Object resolveIdValue(final Object value) {
        if (value instanceof Number) {
            return Long.valueOf(value.toString());
        }

        final Object idValue = new MapSupportingDirectFieldAccessFallbackBeanWrapper(value).getPropertyValue(RegistryCoreConstants.ID_ATTRIBUTE);

        return idValue == null ? null : Long.valueOf(idValue.toString());
    }

    private ManagedTypeWrapper managedTypeWrapper(final Class<?> type) {
        if (!managedTypeWrapperMap.containsKey(type)) {
            managedTypeWrapperMap.put(type, new ManagedTypeWrapper(entityManager.getMetamodel().managedType(type)));
        }

        return managedTypeWrapperMap.get(type);
    }
}
