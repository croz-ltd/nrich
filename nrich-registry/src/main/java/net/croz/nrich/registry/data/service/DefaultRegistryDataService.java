/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.registry.data.service;

import lombok.SneakyThrows;
import net.croz.nrich.registry.api.core.service.RegistryEntityFinderService;
import net.croz.nrich.registry.api.data.interceptor.RegistryDataInterceptor;
import net.croz.nrich.registry.api.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.api.data.service.RegistryDataService;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.model.sort.SortDirection;
import net.croz.nrich.search.api.model.sort.SortProperty;
import net.croz.nrich.search.api.util.PageableUtil;
import net.croz.nrich.search.support.JpaQueryBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.lang.reflect.Field;
import java.util.Collections;
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

    private final RegistryEntityFinderService registryEntityFinderService;

    public DefaultRegistryDataService(EntityManager entityManager, ModelMapper modelMapper, StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter,
                                      RegistryDataConfigurationHolder registryDataConfigurationHolder, List<RegistryDataInterceptor> registryDataInterceptorList,
                                      RegistryEntityFinderService registryEntityFinderService) {
        this.entityManager = entityManager;
        this.modelMapper = modelMapper;
        this.stringToEntityPropertyMapConverter = stringToEntityPropertyMapConverter;
        this.registryDataConfigurationHolder = registryDataConfigurationHolder;
        this.registryDataInterceptorList = registryDataInterceptorList;
        this.classNameQueryBuilderMap = initializeQueryBuilderMap(registryDataConfigurationHolder);
        this.registryEntityFinderService = registryEntityFinderService;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, Page<Object>> listBulk(ListBulkRegistryRequest request) {
        return request.getRegistryRequestList().stream()
            .collect(Collectors.toMap(ListRegistryRequest::getClassFullName, this::list));
    }

    @Transactional(readOnly = true)
    @Override
    public <P> Page<P> list(ListRegistryRequest request) {
        interceptorList().forEach(registryDataInterceptor -> registryDataInterceptor.beforeRegistryList(request));

        return registryListInternal(request);
    }

    @Transactional
    @Override
    public <T> T create(String classFullName, Object entityData) {
        interceptorList().forEach(registryDataInterceptor -> registryDataInterceptor.beforeRegistryCreate(classFullName, entityData));

        @SuppressWarnings("unchecked")
        RegistryDataConfiguration<T, ?> registryDataConfiguration = (RegistryDataConfiguration<T, ?>) registryDataConfigurationHolder.findRegistryConfigurationForClass(classFullName);

        T instance = resolveEntityInstance(registryDataConfiguration.getRegistryType(), entityData);

        modelMapper.map(entityData, instance);

        return entityManager.merge(instance);
    }

    @Transactional
    @Override
    public <T> T update(String classFullName, Object id, Object entityData) {
        interceptorList().forEach(registryDataInterceptor -> registryDataInterceptor.beforeRegistryUpdate(classFullName, id, entityData));

        @SuppressWarnings("unchecked")
        RegistryDataConfiguration<T, ?> registryDataConfiguration = (RegistryDataConfiguration<T, ?>) registryDataConfigurationHolder.findRegistryConfigurationForClass(classFullName);

        ManagedTypeWrapper wrapper = registryDataConfigurationHolder.resolveManagedTypeWrapper(classFullName);

        T instance = registryEntityFinderService.findEntityInstance(registryDataConfiguration.getRegistryType(), id);

        if (wrapper.isIdClassIdentifier() || wrapper.isEmbeddedIdentifier()) {
            entityManager.remove(instance);
            instance = resolveEntityInstance(registryDataConfiguration.getRegistryType(), entityData);

            modelMapper.map(entityData, instance);
        }
        else {
            setIdFieldToOriginalValue(wrapper, entityData, id);

            modelMapper.map(entityData, instance);
        }

        return entityManager.merge(instance);
    }

    @Transactional
    @Override
    public <T> T delete(String classFullName, Object id) {
        interceptorList().forEach(registryDataInterceptor -> registryDataInterceptor.beforeRegistryDelete(classFullName, id));

        @SuppressWarnings("unchecked")
        RegistryDataConfiguration<T, ?> registryDataConfiguration = (RegistryDataConfiguration<T, ?>) registryDataConfigurationHolder.findRegistryConfigurationForClass(classFullName);

        T instance = registryEntityFinderService.findEntityInstance(registryDataConfiguration.getRegistryType(), id);

        entityManager.remove(instance);

        return instance;
    }

    private Map<String, JpaQueryBuilder<?>> initializeQueryBuilderMap(RegistryDataConfigurationHolder registryDataConfigurationHolder) {
        return registryDataConfigurationHolder.getRegistryDataConfigurationList().stream()
            .collect(Collectors.toMap(registryDataConfiguration -> registryDataConfiguration.getRegistryType().getName(),
                registryDataConfiguration -> new JpaQueryBuilder<>(entityManager, registryDataConfiguration.getRegistryType()))
            );
    }

    private List<RegistryDataInterceptor> interceptorList() {
        return Optional.ofNullable(registryDataInterceptorList).orElse(Collections.emptyList());
    }

    private <T, P> Page<P> registryListInternal(ListRegistryRequest request) {
        @SuppressWarnings("unchecked")
        RegistryDataConfiguration<T, P> registryDataConfiguration = (RegistryDataConfiguration<T, P>) registryDataConfigurationHolder.findRegistryConfigurationForClass(request.getClassFullName());
        SearchConfiguration<T, P, Map<String, Object>> searchConfiguration = registryDataConfiguration.getSearchConfiguration();

        @SuppressWarnings("unchecked")
        JpaQueryBuilder<T> queryBuilder = (JpaQueryBuilder<T>) classNameQueryBuilderMap.get(request.getClassFullName());

        ManagedTypeWrapper managedTypeWrapper = registryDataConfigurationHolder.resolveManagedTypeWrapper(request.getClassFullName());

        String idAttributeName = Optional.ofNullable(managedTypeWrapper.getIdAttributeName()).orElseGet(() -> managedTypeWrapper.getIdClassPropertyNameList().get(0));

        Pageable pageable = PageableUtil.convertToPageable(request.getPageNumber(), request.getPageSize(), new SortProperty(idAttributeName, SortDirection.ASC), request.getSortPropertyList());

        Map<String, Object> searchRequestMap = Collections.emptyMap();
        if (request.getSearchParameter() != null) {
            searchRequestMap = stringToEntityPropertyMapConverter.convert(
                request.getSearchParameter().getQuery(), request.getSearchParameter().getPropertyNameList(), managedTypeWrapper.getIdentifiableType(),
                searchConfiguration.getSearchPropertyConfiguration()
            );
        }

        CriteriaQuery<P> query = queryBuilder.buildQuery(searchRequestMap, searchConfiguration, pageable.getSort());

        TypedQuery<P> typedQuery = entityManager.createQuery(query);

        typedQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());

        return PageableExecutionUtils.getPage(typedQuery.getResultList(), pageable, () -> executeCountQuery(queryBuilder, query));
    }

    private long executeCountQuery(JpaQueryBuilder<?> queryBuilder, CriteriaQuery<?> query) {
        CriteriaQuery<Long> countQuery = queryBuilder.convertToCountQuery(query);

        List<Long> totals = entityManager.createQuery(countQuery).getResultList();

        return totals.stream().mapToLong(value -> value == null ? 0L : value).sum();
    }

    @SuppressWarnings("unchecked")
    private <T> T resolveEntityInstance(Class<T> type, Object entityData) {
        if (entityData != null && type.equals(entityData.getClass())) {
            return (T) entityData;
        }

        return BeanUtils.instantiateClass(type);
    }

    // in case users submit a null id value inside entity data it should be reset
    @SneakyThrows
    private void setIdFieldToOriginalValue(ManagedTypeWrapper managedTypeWrapper, Object instance, Object id) {
        Field field = ReflectionUtils.findField(instance.getClass(), managedTypeWrapper.getIdAttributeName());

        if (field == null) {
            return;
        }

        field.setAccessible(true); // NOSONAR

        if (id.equals(field.get(instance))) {
            return;
        }

        field.set(instance, id); // NOSONAR
    }
}
