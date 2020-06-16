package net.croz.nrich.registry.data.service;

import lombok.SneakyThrows;
import net.croz.nrich.registry.core.constants.RegistryCoreConstants;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.interceptor.RegistryDataInterceptor;
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
import java.util.Optional;
import java.util.stream.Collectors;

// TODO better error handling and maybe versioning
public class DefaultRegistryDataService implements RegistryDataService {

    private final EntityManager entityManager;

    private final ModelMapper modelMapper;

    private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    private final RegistryDataConfigurationHolder registryDataConfigurationHolder;

    private final List<RegistryDataInterceptor> registryDataInterceptorList;

    private final Map<String, JpaQueryBuilder<?>> classNameQueryBuilderMap;

    private final Map<String, ManagedTypeWrapper> classNameManagedTypeWrapperMap;

    public DefaultRegistryDataService(final EntityManager entityManager, final ModelMapper modelMapper, final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter, final RegistryDataConfigurationHolder registryDataConfigurationHolder, final List<RegistryDataInterceptor> registryDataInterceptorList) {
        this.entityManager = entityManager;
        this.modelMapper = modelMapper;
        this.stringToEntityPropertyMapConverter = stringToEntityPropertyMapConverter;
        this.registryDataConfigurationHolder = registryDataConfigurationHolder;
        this.registryDataInterceptorList = registryDataInterceptorList;
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
        interceptorList().forEach(registryDataInterceptor -> registryDataInterceptor.beforeRegistryList(request));

        return registryListInternal(request);
    }

    @Transactional
    @Override
    public <T> T create(final CreateRegistryServiceRequest request) {
        interceptorList().forEach(registryDataInterceptor -> registryDataInterceptor.beforeRegistryCreate(request));

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
        interceptorList().forEach(registryDataInterceptor -> registryDataInterceptor.beforeRegistryUpdate(request));

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
        interceptorList().forEach(registryDataInterceptor -> registryDataInterceptor.beforeRegistryDelete(request));

        @SuppressWarnings("unchecked")
        final RegistryDataConfiguration<T, ?> registryDataConfiguration = (RegistryDataConfiguration<T, ?>) registryDataConfigurationHolder.findRegistryConfigurationForClass(request.getClassFullName());

        final T instance = findEntityInstance(registryDataConfiguration.getRegistryType(), request.getId());

        entityManager.remove(instance);

        return instance;
    }

    private Map<String, JpaQueryBuilder<?>> initializeQueryBuilderMap(final RegistryDataConfigurationHolder registryDataConfigurationHolder) {
        return registryDataConfigurationHolder.getRegistryDataConfigurationList().stream()
                .collect(Collectors.toMap(registryDataConfiguration -> registryDataConfiguration.getRegistryType().getName(), registryDataConfiguration -> new JpaQueryBuilder<>(entityManager, registryDataConfiguration.getRegistryType())));
    }

    private Map<String, ManagedTypeWrapper> initializeManagedTypeMap(final RegistryDataConfigurationHolder registryDataConfigurationHolder) {
        return registryDataConfigurationHolder.getRegistryDataConfigurationList().stream()
                .collect(Collectors.toMap(registryDataConfiguration -> registryDataConfiguration.getRegistryType().getName(), registryDataConfiguration -> new ManagedTypeWrapper(entityManager.getMetamodel().managedType(registryDataConfiguration.getRegistryType()))));
    }

    private List<RegistryDataInterceptor> interceptorList() {
        return Optional.ofNullable(registryDataInterceptorList).orElse(Collections.emptyList());
    }

    private <T, P> Page<P> registryListInternal(final ListRegistryRequest request) {
        @SuppressWarnings("unchecked")
        final RegistryDataConfiguration<T, P> registryDataConfiguration = (RegistryDataConfiguration<T, P>) registryDataConfigurationHolder.findRegistryConfigurationForClass(request.getRegistryId());

        @SuppressWarnings("unchecked")
        final JpaQueryBuilder<T> queryBuilder = (JpaQueryBuilder<T>) classNameQueryBuilderMap.get(request.getRegistryId());

        final ManagedType<?> managedType = classNameManagedTypeWrapperMap.get(request.getRegistryId()).getIdentifiableType();

        final Pageable pageable = PageableUtil.convertToPageable(request.getPageNumber(), request.getPageSize(), new SortProperty(RegistryCoreConstants.ID_ATTRIBUTE, SortDirection.ASC), request.getSortPropertyList());

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

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private <T> T resolveEntityInstance(final Class<T> type, final Object entityData) {
        if (entityData != null && type.equals(entityData.getClass())) {
            return (T) entityData;
        }

        return type.newInstance();
    }

    private <T> T findEntityInstance(final Class<T> type, final Object id) {
        Assert.isTrue(id instanceof Map || id instanceof Number, String.format("Id: %s is of not supported type!", id));

        final String wherePart;
        final Map<String, Object> parameterMap = new HashMap<>();
        if (id instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> idMap = ((Map<Object, Object>) id).entrySet().stream()
                    .collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));

            wherePart = idMap.entrySet().stream()
                    .map(entry -> toParameterExpression(entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(" and "));

            idMap.forEach((key, value) -> parameterMap.put(toParameterVariable(key), resolveIdValue(value)));
        }
        else {
            wherePart = String.format(RegistryDataConstants.QUERY_PARAMETER_FORMAT, RegistryCoreConstants.ID_ATTRIBUTE, RegistryCoreConstants.ID_ATTRIBUTE);

            parameterMap.put(RegistryCoreConstants.ID_ATTRIBUTE, Long.valueOf(id.toString()));
        }

        final String fullQuery = String.format(RegistryDataConstants.FIND_QUERY, type.getName(), wherePart);

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
}