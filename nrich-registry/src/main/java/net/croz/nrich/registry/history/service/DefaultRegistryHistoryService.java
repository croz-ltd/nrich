package net.croz.nrich.registry.history.service;

import net.croz.nrich.registry.core.constants.RegistryCoreConstants;
import net.croz.nrich.registry.core.model.PropertyWithType;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;
import net.croz.nrich.registry.history.constants.RegistryHistoryConstants;
import net.croz.nrich.registry.history.model.EntityWithRevision;
import net.croz.nrich.registry.history.model.RevisionInfo;
import net.croz.nrich.registry.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.history.service.RegistryHistoryService;
import net.croz.nrich.search.api.model.SortDirection;
import net.croz.nrich.search.api.model.SortProperty;
import net.croz.nrich.search.support.MapSupportingDirectFieldAccessFallbackBeanWrapper;
import net.croz.nrich.search.util.PageableUtil;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditProperty;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
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

    private final RegistryHistoryConfigurationHolder registryHistoryConfiguration;

    private final ModelMapper modelMapper;

    private final Map<Class<?>, ManagedType<?>> classManagedTypeMap;

    public DefaultRegistryHistoryService(final EntityManager entityManager, final RegistryDataConfigurationHolder registryDataConfigurationHolder, final RegistryHistoryConfigurationHolder registryHistoryConfiguration, final ModelMapper modelMapper) {
        this.entityManager = entityManager;
        this.registryDataConfigurationHolder = registryDataConfigurationHolder;
        this.registryHistoryConfiguration = registryHistoryConfiguration;
        this.modelMapper = modelMapper;
        this.classManagedTypeMap = initializeManagedTypeMap(registryDataConfigurationHolder);
    }

    @Transactional(readOnly = true)
    public <T> Page<EntityWithRevision<T>> historyList(final ListRegistryHistoryRequest request) {

        final AuditQuery auditQuery = createAuditQuery(request);

        addOrder(auditQuery, request.getSortPropertyList());

        final List<?> resultList = auditQuery
                .setFirstResult(request.getPageNumber())
                .setMaxResults(request.getPageSize()).getResultList();

        final List<EntityWithRevision<T>> entityWithRevisionList = convertToEntityRevisionList(resultList);

        final Pageable pageable = PageableUtil.convertToPageable(request.getPageNumber(), request.getPageSize());

        return PageableExecutionUtils.getPage(entityWithRevisionList, pageable, () -> executeCountQuery(createAuditQuery(request)));
    }

    private Map<Class<?>, ManagedType<?>> initializeManagedTypeMap(final RegistryDataConfigurationHolder registryDataConfigurationHolder) {
        if (registryDataConfigurationHolder.getRegistryDataConfigurationList() == null) {
            return Collections.emptyMap();
        }

        return registryDataConfigurationHolder.getRegistryDataConfigurationList().stream()
                .collect(Collectors.toMap(RegistryDataConfiguration::getRegistryType, registryDataConfiguration -> entityManager.getMetamodel().managedType(registryDataConfiguration.getRegistryType())));
    }

    private <T> AuditQuery createAuditQuery(final ListRegistryHistoryRequest request) {
        @SuppressWarnings("unchecked")
        final Class<T> type = (Class<T>) registryDataConfigurationHolder.findRegistryConfigurationForClass(request.getRegistryId()).getRegistryType();

        final AuditQuery auditQuery = AuditReaderFactory.get(entityManager).createQuery().forRevisionsOfEntity(type, false, true);

        if (request.getRegistryRecordId() != null) {
            addIdCondition(auditQuery, request.getRegistryRecordId());
        }

        return auditQuery;
    }

    private long executeCountQuery(final AuditQuery auditQuery) {
        auditQuery.addProjection(AuditEntity.revisionNumber().count());

        return (Long) auditQuery.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    private <T> List<EntityWithRevision<T>> convertToEntityRevisionList(final List<?> resultList) {
        final List<Object[]> objectResultList = (List<Object[]>) resultList;

        return Optional.ofNullable(objectResultList).orElse(Collections.emptyList()).stream()
                .map(value -> new EntityWithRevision<>(initializeEntityOneToOneAssociations((T) value[0]), convertToRevisionInfo(value[1], (RevisionType) value[2])))
                .collect(Collectors.toList());
    }

    private void addIdCondition(final AuditQuery auditQuery, final Object id) {
        Assert.isTrue(id instanceof Map || id instanceof Number, String.format("Id: %s is of not supported type!", id));

        if (id instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> idMap = ((Map<Object, Object>) id).entrySet().stream()
                    .collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));

            idMap.forEach((key, value) -> auditQuery.add(value instanceof Number ? AuditEntity.property(key).eq(resolveIdValue(value)) : AuditEntity.relatedId(key).eq(resolveIdValue(value))));
        }
        else {
            auditQuery.add(AuditEntity.id().eq(Long.valueOf(id.toString())));
        }
    }

    private void addOrder(final AuditQuery auditQuery, final List<SortProperty> sortPropertyList) {
        if (CollectionUtils.isEmpty(sortPropertyList)) {
            return;
        }

        sortPropertyList.forEach(sortProperty -> {
            final AuditProperty<?> auditProperty = resolveAuditProperty(sortProperty.getName());

            if (sortProperty.getDirection() == SortDirection.ASC) {
                auditQuery.addOrder(auditProperty.asc());
            }
            else {
                auditQuery.addOrder(auditProperty.desc());
            }
        });
    }

    private AuditProperty<?> resolveAuditProperty(final String sortProperty) {
        final AuditProperty<?> auditProperty;

        if (RegistryHistoryConstants.REVISION_NUMBER_PROPERTY.equals(sortProperty)) {
            auditProperty = AuditEntity.revisionNumber();
        }
        else if (RegistryHistoryConstants.REVISION_TYPE_PROPERTY.equals(sortProperty)) {
            auditProperty = AuditEntity.revisionType();
        }
        else if (sortProperty.startsWith(RegistryHistoryConstants.REVISION_PROPERTY_PREFIX)) {
            auditProperty = AuditEntity.revisionProperty(sortProperty);
        }
        else {
            auditProperty = AuditEntity.property(sortProperty);
        }

        return auditProperty;
    }

    private Long resolveIdValue(final Object value) {
        if (value instanceof Number) {
            return Long.valueOf(value.toString());
        }

        final Object idValue = new MapSupportingDirectFieldAccessFallbackBeanWrapper(value).getPropertyValue(RegistryCoreConstants.ID_ATTRIBUTE);

        return idValue == null ? null : Long.valueOf(idValue.toString());
    }

    private RevisionInfo convertToRevisionInfo(final Object revisionEntity, final RevisionType revisionType) {
        final MapSupportingDirectFieldAccessFallbackBeanWrapper directFieldAccessFallbackBeanWrapper = new MapSupportingDirectFieldAccessFallbackBeanWrapper(revisionEntity);

        final Object revisionNumber = directFieldAccessFallbackBeanWrapper.getPropertyValue(registryHistoryConfiguration.getRevisionNumberProperty().getName());

        final Object revisionDate = directFieldAccessFallbackBeanWrapper.getPropertyValue(registryHistoryConfiguration.getRevisionTimestampProperty().getName());

        Assert.isTrue(revisionNumber != null && revisionDate != null, "Revision number or revision date are empty!");

        final Instant revisionDateAsInstant = revisionDate instanceof Long ? Instant.ofEpochMilli((long) revisionDate) : ((Date) revisionDate).toInstant();

        final Map<String, Object> additionalRevisionPropertyMap = registryHistoryConfiguration.getRevisionAdditionalPropertyList().stream()
                .collect(Collectors.toMap(PropertyWithType::getName, propertyWithType -> directFieldAccessFallbackBeanWrapper.getPropertyValue(propertyWithType.getName())));

        return new RevisionInfo(Long.valueOf(revisionNumber.toString()), revisionDateAsInstant, revisionType.name(), additionalRevisionPropertyMap);
    }

    // TODO not happy about this solution, think of a better one
    private <T> T initializeEntityOneToOneAssociations(final T entity) {
        final ManagedType<?> managedType = classManagedTypeMap.get(entity.getClass());

        final MapSupportingDirectFieldAccessFallbackBeanWrapper mapSupportingDirectFieldAccessFallbackBeanWrapper = new MapSupportingDirectFieldAccessFallbackBeanWrapper(entity);

        managedType.getAttributes().forEach(attribute -> {

            if (Attribute.PersistentAttributeType.ONE_TO_ONE.equals(attribute.getPersistentAttributeType())) {
                final String attributeName = attribute.getName();
                final Object attributeValue = mapSupportingDirectFieldAccessFallbackBeanWrapper.getPropertyValue(attributeName);

                if (attributeValue == null) {
                    return;
                }

                final Object deProxiedValue = BeanUtils.instantiateClass(attribute.getJavaType());

                modelMapper.map(attributeValue, deProxiedValue);

                mapSupportingDirectFieldAccessFallbackBeanWrapper.setPropertyValue(attributeName, deProxiedValue);
            }
        });

        return entity;
    }
}
