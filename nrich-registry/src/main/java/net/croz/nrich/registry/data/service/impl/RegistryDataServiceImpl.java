package net.croz.nrich.registry.data.service.impl;

import lombok.SneakyThrows;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.model.RegistryDataConfiguration;
import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryServiceRequest;
import net.croz.nrich.registry.data.service.RegistryDataService;
import net.croz.nrich.registry.data.util.RegistrySearchConfigurationUtil;
import net.croz.nrich.search.api.model.SortDirection;
import net.croz.nrich.search.api.model.SortProperty;
import net.croz.nrich.search.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.support.JpaQueryBuilder;
import net.croz.nrich.search.util.PageableUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.ManagedType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO handling of composite id attributes, better error handling and maybe versioning
public class RegistryDataServiceImpl implements RegistryDataService {

    private final EntityManager entityManager;

    private final ModelMapper modelMapper;

    private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    private final List<RegistryDataConfiguration<?, ?>> registryDataConfigurationList;

    private final Map<String, JpaQueryBuilder<?>> classNameQueryBuilderMap;

    public RegistryDataServiceImpl(final EntityManager entityManager, final ModelMapper modelMapper, final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter, final List<RegistryDataConfiguration<?, ?>> registryDataConfigurationList) {
        this.entityManager = entityManager;
        this.modelMapper = modelMapper;
        this.stringToEntityPropertyMapConverter = stringToEntityPropertyMapConverter;
        this.registryDataConfigurationList = registryDataConfigurationList;
        this.classNameQueryBuilderMap = initializeQueryBuilderMap(registryDataConfigurationList);
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
        final RegistryDataConfiguration<T, ?> registryDataConfiguration = (RegistryDataConfiguration<T, ?>) RegistrySearchConfigurationUtil.findRegistryConfigurationForClass(registryDataConfigurationList, request.getClassFullName());

        final T instance = resolveEntityInstance(registryDataConfiguration.getRegistryType(), request.getEntityData());

        modelMapper.map(request.getEntityData(), instance);

        entityManager.persist(instance);

        return instance;
    }

    @Transactional
    @Override
    public <T> T update(final UpdateRegistryServiceRequest request) {
        @SuppressWarnings("unchecked")
        final RegistryDataConfiguration<T, ?> registryDataConfiguration = (RegistryDataConfiguration<T, ?>) RegistrySearchConfigurationUtil.findRegistryConfigurationForClass(registryDataConfigurationList, request.getClassFullName());

        final T instance = entityManager.find(registryDataConfiguration.getRegistryType(), request.getId());

        modelMapper.map(request.getEntityData(), instance);

        entityManager.persist(instance);

        return instance;
    }

    @Transactional
    @Override
    public boolean delete(final DeleteRegistryRequest request) {
        RegistrySearchConfigurationUtil.verifyConfigurationExists(registryDataConfigurationList, request.getClassFullName());

        final String fullQuery = String.format(RegistryDataConstants.DELETE_QUERY, request.getClassFullName());

        final int updateCount = entityManager.createQuery(fullQuery).setParameter(RegistryDataConstants.ID_ATTRIBUTE, request.getId()).executeUpdate();

        return updateCount > 0;
    }

    private <T, P> Page<P> registryListInternal(final ListRegistryRequest request) {
        @SuppressWarnings("unchecked")
        final RegistryDataConfiguration<T, P> registryDataConfiguration = (RegistryDataConfiguration<T, P>) RegistrySearchConfigurationUtil.findRegistryConfigurationForClass(registryDataConfigurationList, request.getClassFullName());

        @SuppressWarnings("unchecked")
        final JpaQueryBuilder<T> queryBuilder = (JpaQueryBuilder<T>) classNameQueryBuilderMap.get(request.getClassFullName());

        final ManagedType<?> managedType = resolveManagedType(registryDataConfiguration);

        final Pageable pageable = PageableUtil.convertToPageable(request.getPageNumber(), request.getPageSize(), new SortProperty(RegistryDataConstants.ID_ATTRIBUTE, SortDirection.ASC), request.getSortPropertyList());

        Map<String, Object> searchRequestMap = Collections.emptyMap();
        if (request.getSearchParameter() != null) {
            searchRequestMap = stringToEntityPropertyMapConverter.convert(request.getSearchParameter().getQuery(), request.getSearchParameter().getPropertyNameList(), managedType);
        }

        final CriteriaQuery<P> query = queryBuilder.buildQuery(searchRequestMap, registryDataConfiguration.getSearchConfiguration(), pageable.getSort());

        final TypedQuery<P> typedQuery = entityManager.createQuery(query);

        if (pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());

            return PageableExecutionUtils.getPage(typedQuery.getResultList(), pageable, () -> executeCountQuery(queryBuilder, query));
        }

        return new PageImpl<>(typedQuery.getResultList());
    }

    private long executeCountQuery(final JpaQueryBuilder<?> queryBuilder, final CriteriaQuery<?> query) {
        final CriteriaQuery<Long> countQuery = queryBuilder.convertToCountQuery(query);

        final List<Long> totals = entityManager.createQuery(countQuery).getResultList();

        return totals.stream().mapToLong(value -> value == null ? 0L : value).sum();
    }

    private Map<String, JpaQueryBuilder<?>> initializeQueryBuilderMap(final List<RegistryDataConfiguration<?, ?>> registryDataConfigurationList) {
        if (registryDataConfigurationList == null) {
            return Collections.emptyMap();
        }

        return registryDataConfigurationList.stream()
                .collect(Collectors.toMap(registryDataConfiguration -> registryDataConfiguration.getRegistryType().getName(), registryDataConfiguration -> new JpaQueryBuilder<>(entityManager, registryDataConfiguration.getRegistryType())));
    }

    private ManagedType<?> resolveManagedType(final RegistryDataConfiguration<?, ?> registryDataConfiguration) {
        return entityManager.getMetamodel().managedType(registryDataConfiguration.getRegistryType());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private <T> T resolveEntityInstance(final Class<T> type, final Object entityData) {
        if (type.equals(entityData)) {
            return (T) entityData;
        }
        return type.newInstance();
    }
}
