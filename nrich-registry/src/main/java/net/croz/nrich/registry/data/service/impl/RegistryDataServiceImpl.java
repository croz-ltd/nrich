package net.croz.nrich.registry.data.service.impl;

import lombok.SneakyThrows;
import net.croz.nrich.registry.core.model.ManagedTypeWrapper;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.data.request.BulkListRegistryRequest;
import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryServiceRequest;
import net.croz.nrich.registry.data.service.RegistryDataService;
import net.croz.nrich.search.api.model.SortDirection;
import net.croz.nrich.search.api.model.SortProperty;
import net.croz.nrich.search.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.support.JpaQueryBuilder;
import net.croz.nrich.search.support.MapSupportingDirectFieldAccessFallbackBeanWrapper;
import net.croz.nrich.search.util.PageableUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.ManagedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO better error handling and maybe versioning
public class RegistryDataServiceImpl implements RegistryDataService {

    private final EntityManager entityManager;

    private final ModelMapper modelMapper;

    private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    private final RegistryDataConfigurationHolder registryDataConfigurationHolder;

    private final Map<String, JpaQueryBuilder<?>> classNameQueryBuilderMap;

    private final Map<String, ManagedTypeWrapper> classNameManagedTypeWrapperMap;

    public RegistryDataServiceImpl(final EntityManager entityManager, final ModelMapper modelMapper, final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter, RegistryDataConfigurationHolder registryDataConfigurationHolder) {
        this.entityManager = entityManager;
        this.modelMapper = modelMapper;
        this.stringToEntityPropertyMapConverter = stringToEntityPropertyMapConverter;
        this.registryDataConfigurationHolder = registryDataConfigurationHolder;
        this.classNameQueryBuilderMap = initializeQueryBuilderMap(registryDataConfigurationHolder);
        this.classNameManagedTypeWrapperMap = initializeManagedTypeMap(registryDataConfigurationHolder);
    }

    @Transactional(readOnly = true)
    @Override
    public <P> Map<String, Page<P>> bulkList(final BulkListRegistryRequest request) {
        return request.getRegistryRequestList().stream()
                .collect(Collectors.toMap(ListRegistryRequest::getRegistryId, this::list));
    }

    @Transactional(readOnly = true)
    @Override
    public <P> Page<P> list(final ListRegistryRequest request) {
        return registryListInternal(request);
    }

    @Transactional
    @Override
    public <T> T create(final CreateRegistryServiceRequest request) {
        @SuppressWarnings("unchecked")
        final RegistryDataConfiguration<T, ?> registryDataConfiguration = (RegistryDataConfiguration<T, ?>) registryDataConfigurationHolder.findRegistryConfigurationForClass(request.getClassFullName());

        final T instance = resolveEntityInstance(registryDataConfiguration.getRegistryType(), request.getEntityData());

        modelMapper.map(request.getEntityData(), instance);

        entityManager.persist(instance);

        return instance;
    }

    @Transactional
    @Override
    public <T> T update(final UpdateRegistryServiceRequest request) {
        @SuppressWarnings("unchecked")
        final RegistryDataConfiguration<T, ?> registryDataConfiguration = (RegistryDataConfiguration<T, ?>) registryDataConfigurationHolder.findRegistryConfigurationForClass(request.getClassFullName());

        final ManagedTypeWrapper wrapper = classNameManagedTypeWrapperMap.get(request.getClassFullName());

        T instance = findEntityInstance(registryDataConfiguration.getRegistryType(), request.getId());

        if (wrapper.isCompositeIdentity()) {
            entityManager.remove(instance);
            instance = resolveEntityInstance(registryDataConfiguration.getRegistryType(), request.getEntityData());
        }

        modelMapper.map(request.getEntityData(), instance);

        entityManager.persist(instance);

        return instance;
    }

    @Transactional
    @Override
    public <T> T delete(final DeleteRegistryRequest request) {
        @SuppressWarnings("unchecked")
        final RegistryDataConfiguration<T, ?> registryDataConfiguration = (RegistryDataConfiguration<T, ?>) registryDataConfigurationHolder.findRegistryConfigurationForClass(request.getClassFullName());

        final T instance = findEntityInstance(registryDataConfiguration.getRegistryType(), request.getId());

        entityManager.remove(instance);

        return instance;
    }

    private <T, P> Page<P> registryListInternal(final ListRegistryRequest request) {
        @SuppressWarnings("unchecked")
        final RegistryDataConfiguration<T, P> registryDataConfiguration = (RegistryDataConfiguration<T, P>) registryDataConfigurationHolder.findRegistryConfigurationForClass(request.getRegistryId());

        @SuppressWarnings("unchecked")
        final JpaQueryBuilder<T> queryBuilder = (JpaQueryBuilder<T>) classNameQueryBuilderMap.get(request.getRegistryId());

        final ManagedType<?> managedType = classNameManagedTypeWrapperMap.get(request.getRegistryId()).getIdentifiableType();

        final Pageable pageable = PageableUtil.convertToPageable(request.getPageNumber(), request.getPageSize(), new SortProperty(RegistryDataConstants.ID_ATTRIBUTE, SortDirection.ASC), request.getSortPropertyList());

        Map<String, Object> searchRequestMap = Collections.emptyMap();
        if (request.getSearchParameter() != null) {
            searchRequestMap = stringToEntityPropertyMapConverter.convert(request.getSearchParameter().getQuery(), request.getSearchParameter().getPropertyNameList(), managedType);
        }

        final CriteriaQuery<P> query = queryBuilder.buildQuery(searchRequestMap, registryDataConfiguration.getSearchConfiguration(), pageable.getSort());

        final TypedQuery<P> typedQuery = entityManager.createQuery(query);

        typedQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());

        return PageableExecutionUtils.getPage(typedQuery.getResultList(), pageable, () -> executeCountQuery(queryBuilder, query));
    }

    private long executeCountQuery(final JpaQueryBuilder<?> queryBuilder, final CriteriaQuery<?> query) {
        final CriteriaQuery<Long> countQuery = queryBuilder.convertToCountQuery(query);

        final List<Long> totals = entityManager.createQuery(countQuery).getResultList();

        return totals.stream().mapToLong(value -> value == null ? 0L : value).sum();
    }

    private Map<String, JpaQueryBuilder<?>> initializeQueryBuilderMap(final RegistryDataConfigurationHolder registryDataConfigurationHolder) {
        if (registryDataConfigurationHolder.getRegistryDataConfigurationList() == null) {
            return Collections.emptyMap();
        }

        return registryDataConfigurationHolder.getRegistryDataConfigurationList().stream()
                .collect(Collectors.toMap(registryDataConfiguration -> registryDataConfiguration.getRegistryType().getName(), registryDataConfiguration -> new JpaQueryBuilder<>(entityManager, registryDataConfiguration.getRegistryType())));
    }

    private Map<String, ManagedTypeWrapper> initializeManagedTypeMap(final RegistryDataConfigurationHolder registryDataConfigurationHolder) {
        if (registryDataConfigurationHolder.getRegistryDataConfigurationList() == null) {
            return Collections.emptyMap();
        }

        return registryDataConfigurationHolder.getRegistryDataConfigurationList().stream()
                .collect(Collectors.toMap(registryDataConfiguration -> registryDataConfiguration.getRegistryType().getName(), registryDataConfiguration -> new ManagedTypeWrapper(entityManager.getMetamodel().managedType(registryDataConfiguration.getRegistryType()))));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private <T> T resolveEntityInstance(final Class<T> type, final Object entityData) {
        if (type.equals(entityData)) {
            return (T) entityData;
        }
        return type.newInstance();
    }

    protected <T> T findEntityInstance(final Class<T> type, final Object id) {
        Assert.isTrue(id instanceof Map || id instanceof Number, String.format("Id: %s is of not supported type!", id));

        final String wherePart;
        final Map<String, Object> parameterMap = new HashMap<>();
        if (id instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> idMap = ((Map<Object, Object>) id).entrySet().stream()
                    .collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));

            wherePart = idMap.keySet().stream()
                    .map(key -> String.format(RegistryDataConstants.QUERY_PARAMETER_FORMAT, key + "." + RegistryDataConstants.ID_ATTRIBUTE, toParameterVariable(key)))
                    .collect(Collectors.joining(" and "));

            idMap.forEach((key, value) -> parameterMap.put(toParameterVariable(key), resolveIdValue(value)));
        }
        else {
            wherePart = String.format(RegistryDataConstants.QUERY_PARAMETER_FORMAT, RegistryDataConstants.ID_ATTRIBUTE, RegistryDataConstants.ID_ATTRIBUTE);

            parameterMap.put(RegistryDataConstants.ID_ATTRIBUTE, Long.valueOf(id.toString()));
        }

        final String fullQuery = String.format(RegistryDataConstants.FIND_QUERY, type.getName(), wherePart);

        @SuppressWarnings("unchecked")
        final TypedQuery<T> query = (TypedQuery<T>) entityManager.createQuery(fullQuery);

        parameterMap.forEach(query::setParameter);

        return query.getSingleResult();
    }

    private String toParameterVariable(final Object key) {
        final String[] keyList = key.toString().split("\\.");

        return Arrays.stream(keyList).map(StringUtils::capitalize).collect(Collectors.joining());
    }

    private Long resolveIdValue(final Object value) {
        final Object idValue = new MapSupportingDirectFieldAccessFallbackBeanWrapper(value).getPropertyValue("id");

        return idValue == null ? null : Long.valueOf(idValue.toString());
    }
}
