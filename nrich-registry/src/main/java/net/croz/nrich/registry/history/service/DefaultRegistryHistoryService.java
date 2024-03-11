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

package net.croz.nrich.registry.history.service;

import net.croz.nrich.registry.api.core.service.RegistryEntityFinderService;
import net.croz.nrich.registry.api.history.model.EntityWithRevision;
import net.croz.nrich.registry.api.history.model.RevisionInfo;
import net.croz.nrich.registry.api.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.api.history.service.RegistryHistoryService;
import net.croz.nrich.registry.core.constants.RegistryEnversConstants;
import net.croz.nrich.registry.core.model.PropertyWithType;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;
import net.croz.nrich.search.api.model.sort.SortDirection;
import net.croz.nrich.search.api.model.sort.SortProperty;
import net.croz.nrich.search.api.util.PageableUtil;
import net.croz.nrich.search.bean.MapSupportingDirectFieldAccessFallbackBeanWrapper;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditProperty;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultRegistryHistoryService implements RegistryHistoryService {

    private final EntityManager entityManager;

    private final RegistryDataConfigurationHolder registryDataConfigurationHolder;

    private final RegistryHistoryConfigurationHolder registryHistoryConfigurationHolder;

    private final ModelMapper modelMapper;

    private final Map<Class<?>, ManagedType<?>> classManagedTypeMap;

    private final RegistryEntityFinderService registryEntityFinderService;

    public DefaultRegistryHistoryService(EntityManager entityManager, RegistryDataConfigurationHolder registryDataConfigurationHolder,
                                         RegistryHistoryConfigurationHolder registryHistoryConfigurationHolder, ModelMapper modelMapper, RegistryEntityFinderService registryEntityFinderService) {
        this.entityManager = entityManager;
        this.registryDataConfigurationHolder = registryDataConfigurationHolder;
        this.registryHistoryConfigurationHolder = registryHistoryConfigurationHolder;
        this.modelMapper = modelMapper;
        this.classManagedTypeMap = initializeManagedTypeMap(registryDataConfigurationHolder);
        this.registryEntityFinderService = registryEntityFinderService;
    }

    @Transactional(readOnly = true)
    public <T> Page<EntityWithRevision<T>> historyList(ListRegistryHistoryRequest request) {
        AuditQuery auditQuery = createAuditQuery(request);

        addOrder(auditQuery, request.sortPropertyList());

        List<?> resultList = auditQuery
            .setFirstResult(request.pageNumber())
            .setMaxResults(request.pageSize()).getResultList();

        List<EntityWithRevision<T>> entityWithRevisionList = convertToEntityRevisionList(resultList);

        Pageable pageable = PageableUtil.convertToPageable(request.pageNumber(), request.pageSize());

        return PageableExecutionUtils.getPage(entityWithRevisionList, pageable, () -> executeCountQuery(createAuditQuery(request)));
    }

    private Map<Class<?>, ManagedType<?>> initializeManagedTypeMap(RegistryDataConfigurationHolder registryDataConfigurationHolder) {
        if (registryDataConfigurationHolder.registryDataConfigurationList() == null) {
            return Collections.emptyMap();
        }

        return registryDataConfigurationHolder.registryDataConfigurationList().stream()
            .collect(Collectors.toMap(RegistryDataConfiguration::registryType, registryDataConfiguration -> entityManager.getMetamodel().managedType(registryDataConfiguration.registryType())));
    }

    private <T> AuditQuery createAuditQuery(ListRegistryHistoryRequest request) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) registryDataConfigurationHolder.findRegistryConfigurationForClass(request.classFullName()).registryType();

        AuditQuery auditQuery = AuditReaderFactory.get(entityManager).createQuery().forRevisionsOfEntity(type, false, true);

        if (request.registryRecordId() != null) {
            addIdCondition(type, auditQuery, request.registryRecordId());
        }

        return auditQuery;
    }

    private long executeCountQuery(AuditQuery auditQuery) {
        auditQuery.addProjection(AuditEntity.revisionNumber().count());

        return (Long) auditQuery.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    private <T> List<EntityWithRevision<T>> convertToEntityRevisionList(List<?> resultList) {
        List<Object[]> objectResultList = (List<Object[]>) resultList;

        return Optional.ofNullable(objectResultList).orElse(Collections.emptyList()).stream()
            .map(value -> new EntityWithRevision<>(initializeEntitySingularAssociations((T) value[0]), convertToRevisionInfo(value[1], (RevisionType) value[2])))
            .toList();
    }

    private void addIdCondition(Class<?> type, AuditQuery auditQuery, Object id) {
        Map<String, Object> idParameterMap = registryEntityFinderService.resolveIdParameterMap(type, id);

        if (idParameterMap.size() == 1) {
            idParameterMap.forEach((key, value) -> auditQuery.add(AuditEntity.id().eq(value)));
        }
        else {
            idParameterMap.forEach((key, value) -> auditQuery.add(AuditEntity.property(key).eq(value)));
        }
    }

    private void addOrder(AuditQuery auditQuery, List<SortProperty> sortPropertyList) {
        if (CollectionUtils.isEmpty(sortPropertyList)) {
            return;
        }

        sortPropertyList.forEach(sortProperty -> {
            AuditProperty<?> auditProperty = resolveAuditProperty(sortProperty.getProperty());

            if (sortProperty.getDirection() == SortDirection.ASC) {
                auditQuery.addOrder(auditProperty.asc());
            }
            else {
                auditQuery.addOrder(auditProperty.desc());
            }
        });
    }

    private AuditProperty<?> resolveAuditProperty(String sortProperty) {
        PropertyWithType revisionProperty = findByName(sortProperty);

        AuditProperty<?> auditProperty;
        if (RegistryEnversConstants.REVISION_NUMBER_PROPERTY_NAME.equals(sortProperty)) {
            auditProperty = AuditEntity.revisionNumber();
        }
        else if (RegistryEnversConstants.REVISION_TYPE_PROPERTY_NAME.equals(sortProperty)) {
            auditProperty = AuditEntity.revisionType();
        }
        else if (RegistryEnversConstants.REVISION_TIMESTAMP_PROPERTY_NAME.equals(sortProperty)) {
            auditProperty = AuditEntity.revisionProperty(registryHistoryConfigurationHolder.revisionTimestampProperty().originalName());
        }
        else if (revisionProperty != null) {
            auditProperty = AuditEntity.revisionProperty(revisionProperty.originalName());
        }
        else {
            auditProperty = AuditEntity.property(sortProperty);
        }

        return auditProperty;
    }

    private RevisionInfo convertToRevisionInfo(Object revisionEntity, RevisionType revisionType) {
        MapSupportingDirectFieldAccessFallbackBeanWrapper directFieldAccessFallbackBeanWrapper = new MapSupportingDirectFieldAccessFallbackBeanWrapper(revisionEntity);

        Object revisionNumber = directFieldAccessFallbackBeanWrapper.getPropertyValue(registryHistoryConfigurationHolder.revisionNumberProperty().originalName());
        Object revisionDate = directFieldAccessFallbackBeanWrapper.getPropertyValue(registryHistoryConfigurationHolder.revisionTimestampProperty().originalName());

        Assert.isTrue(revisionNumber != null && revisionDate != null, "Revision number or revision date are empty!");

        Instant revisionDateAsInstant = revisionDate instanceof Long revisionDateMillis ? Instant.ofEpochMilli(revisionDateMillis) : ((Date) revisionDate).toInstant();

        Map<String, Object> additionalRevisionPropertyMap = registryHistoryConfigurationHolder.revisionAdditionalPropertyList().stream()
            .collect(Collectors.toMap(PropertyWithType::name, propertyWithType -> directFieldAccessFallbackBeanWrapper.getPropertyValue(propertyWithType.originalName())));

        return new RevisionInfo(Long.valueOf(revisionNumber.toString()), revisionDateAsInstant, revisionType.name(), additionalRevisionPropertyMap);
    }

    // TODO not happy about this solution, think of a better one
    private <T> T initializeEntitySingularAssociations(T entity) {
        ManagedType<?> managedType = classManagedTypeMap.get(entity.getClass());

        MapSupportingDirectFieldAccessFallbackBeanWrapper mapSupportingDirectFieldAccessFallbackBeanWrapper = new MapSupportingDirectFieldAccessFallbackBeanWrapper(entity);

        managedType.getSingularAttributes().stream().filter(Attribute::isAssociation).forEach(attribute -> {
            String attributeName = attribute.getName();
            Object attributeValue = mapSupportingDirectFieldAccessFallbackBeanWrapper.getPropertyValue(attributeName);

            if (attributeValue == null) {
                return;
            }

            Object deProxiedValue = BeanUtils.instantiateClass(attribute.getJavaType());

            modelMapper.map(attributeValue, deProxiedValue);

            mapSupportingDirectFieldAccessFallbackBeanWrapper.setPropertyValue(attributeName, deProxiedValue);
        });

        return entity;
    }

    private PropertyWithType findByName(String name) {
        return registryHistoryConfigurationHolder.revisionAdditionalPropertyList().stream()
            .filter(value -> name.equals(value.name()))
            .findFirst()
            .orElse(null);
    }
}
