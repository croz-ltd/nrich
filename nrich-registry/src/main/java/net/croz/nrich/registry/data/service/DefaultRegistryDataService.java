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

import net.croz.nrich.registry.api.core.service.RegistryEntityFinderService;
import net.croz.nrich.registry.api.data.interceptor.RegistryDataInterceptor;
import net.croz.nrich.registry.api.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.api.data.service.RegistryDataService;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.registry.data.util.HibernateUtil;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.model.property.SearchPropertyConfiguration;
import net.croz.nrich.search.api.model.sort.SortDirection;
import net.croz.nrich.search.api.model.sort.SortProperty;
import net.croz.nrich.search.api.util.PageableUtil;
import net.croz.nrich.search.support.JpaQueryBuilder;
import net.croz.nrich.search.util.PathResolvingUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
        return request.registryRequestList().stream()
            .collect(Collectors.toMap(ListRegistryRequest::classFullName, this::list));
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

        T instance = resolveEntityInstance(registryDataConfiguration.registryType(), entityData);

        modelMapper.map(entityData, instance);

        return mergeAndInitializeEntity(instance);
    }

    @Transactional
    @Override
    public <T> T update(String classFullName, Object id, Object entityData) {
        interceptorList().forEach(registryDataInterceptor -> registryDataInterceptor.beforeRegistryUpdate(classFullName, id, entityData));

        @SuppressWarnings("unchecked")
        RegistryDataConfiguration<T, ?> registryDataConfiguration = (RegistryDataConfiguration<T, ?>) registryDataConfigurationHolder.findRegistryConfigurationForClass(classFullName);

        ManagedTypeWrapper wrapper = registryDataConfigurationHolder.resolveManagedTypeWrapper(classFullName);

        T instance = registryEntityFinderService.findEntityInstance(registryDataConfiguration.registryType(), id);

        if (wrapper.isIdClassIdentifier() || wrapper.isEmbeddedIdentifier()) {
            entityManager.remove(instance);
            instance = resolveEntityInstance(registryDataConfiguration.registryType(), entityData);
        }
        else {
            setIdFieldToOriginalValue(wrapper, entityData, id);
        }

        clearAssociationValues(wrapper, instance, entityData);

        modelMapper.map(entityData, instance);

        return mergeAndInitializeEntity(instance);
    }

    @Transactional
    @Override
    public <T> T delete(String classFullName, Object id) {
        interceptorList().forEach(registryDataInterceptor -> registryDataInterceptor.beforeRegistryDelete(classFullName, id));

        @SuppressWarnings("unchecked")
        RegistryDataConfiguration<T, ?> registryDataConfiguration = (RegistryDataConfiguration<T, ?>) registryDataConfigurationHolder.findRegistryConfigurationForClass(classFullName);

        T instance = registryEntityFinderService.findEntityInstance(registryDataConfiguration.registryType(), id);

        entityManager.remove(instance);

        return instance;
    }

    private Map<String, JpaQueryBuilder<?>> initializeQueryBuilderMap(RegistryDataConfigurationHolder registryDataConfigurationHolder) {
        return registryDataConfigurationHolder.registryDataConfigurationList().stream()
            .collect(Collectors.toMap(registryDataConfiguration -> registryDataConfiguration.registryType().getName(),
                registryDataConfiguration -> new JpaQueryBuilder<>(entityManager, registryDataConfiguration.registryType()))
            );
    }

    private List<RegistryDataInterceptor> interceptorList() {
        return Optional.ofNullable(registryDataInterceptorList).orElse(Collections.emptyList());
    }

    private <T, P> Page<P> registryListInternal(ListRegistryRequest request) {
        @SuppressWarnings("unchecked")
        RegistryDataConfiguration<T, P> registryDataConfiguration = (RegistryDataConfiguration<T, P>) registryDataConfigurationHolder.findRegistryConfigurationForClass(request.classFullName());
        SearchConfiguration<T, P, Map<String, Object>> searchConfiguration = registryDataConfiguration.searchConfiguration();

        @SuppressWarnings("unchecked")
        JpaQueryBuilder<T> queryBuilder = (JpaQueryBuilder<T>) classNameQueryBuilderMap.get(request.classFullName());

        ManagedTypeWrapper managedTypeWrapper = registryDataConfigurationHolder.resolveManagedTypeWrapper(request.classFullName());

        String idAttributeName = Optional.ofNullable(managedTypeWrapper.getIdAttributeName()).orElseGet(() -> managedTypeWrapper.getIdClassPropertyNameList().get(0));

        Pageable pageable = PageableUtil.convertToPageable(request.pageNumber(), request.pageSize(), new SortProperty(idAttributeName, SortDirection.ASC), request.sortPropertyList());

        Map<String, Object> searchRequestMap = resolveSearchRequestMap(managedTypeWrapper, request, searchConfiguration.getSearchPropertyConfiguration());
        CriteriaQuery<P> query = queryBuilder.buildQuery(searchRequestMap, searchConfiguration, pageable.getSort());

        TypedQuery<P> typedQuery = entityManager.createQuery(query);

        typedQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());

        return PageableExecutionUtils.getPage(typedQuery.getResultList(), pageable, () -> executeCountQuery(queryBuilder, searchRequestMap, searchConfiguration));
    }

    private Map<String, Object> resolveSearchRequestMap(ManagedTypeWrapper managedTypeWrapper, ListRegistryRequest request, SearchPropertyConfiguration searchPropertyConfiguration) {
        Map<String, Object> searchRequestMap = Collections.emptyMap();
        if (request.searchParameter() != null) {
            searchRequestMap = stringToEntityPropertyMapConverter.convert(
                request.searchParameter().query(), request.searchParameter().propertyNameList(), managedTypeWrapper.getIdentifiableType(),
                searchPropertyConfiguration
            );
        }

        return searchRequestMap;
    }

    private <T, P> long executeCountQuery(JpaQueryBuilder<T> queryBuilder, Map<String, Object> searchRequestMap, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        CriteriaQuery<Long> countQuery = queryBuilder.buildCountQuery(searchRequestMap, searchConfiguration);

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

    // in case users submit a null id value inside entity data it should be reset, this seems to be the simplest way of doing that
    private void setIdFieldToOriginalValue(ManagedTypeWrapper managedTypeWrapper, Object entityData, Object id) {
        Map<String, Object> idValueMap = Collections.singletonMap(managedTypeWrapper.getIdAttributeName(), id);

        modelMapper.map(idValueMap, entityData);
    }

    private void clearAssociationValues(ManagedTypeWrapper managedTypeWrapper, Object instance, Object entityData) {
        Set<String> associationList = managedTypeWrapper.getSingularAssociationList().stream()
            .map(association -> PathResolvingUtil.convertToPathList(association.path())[0])
            .collect(Collectors.toSet());

        associationList.forEach(association -> clearValue(instance, entityData, association));
    }

    private <T> T mergeAndInitializeEntity(T instance) {
        T mergedInstance = entityManager.merge(instance);

        HibernateUtil.initialize(mergedInstance);

        return mergedInstance;
    }

    // when updating associations ModelMapper will use reflection and directly change the id value which causes
    // org.springframework.orm.jpa.JpaSystemException: identifier of an instance of ... was changed ... from ... to ... exception
    private void clearValue(Object instance, Object entityData, String path) {
        BeanWrapper entityDataWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entityData);

        if (entityDataWrapper.isReadableProperty(path) && entityDataWrapper.getPropertyValue(path) != null) {
            PropertyAccessorFactory.forBeanPropertyAccess(instance).setPropertyValue(path, null);
        }
    }
}
