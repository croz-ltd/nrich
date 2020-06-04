package net.croz.nrich.registry.history.service.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.core.constants.RegistryCoreConstants;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
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
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RegistryHistoryServiceImpl implements RegistryHistoryService {

    private final EntityManager entityManager;

    private final RegistryDataConfigurationHolder registryDataConfigurationHolder;

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
                .map(value -> new EntityWithRevision<>((T) value[0], new RevisionInfo((DefaultRevisionEntity) value[1], (RevisionType) value[2])))
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
            auditQuery.add(AuditEntity.id().eq(id));
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
        if (sortProperty.startsWith(RegistryHistoryConstants.REVISION_PROPERTY_PREFIX)) {
            auditProperty = AuditEntity.revisionProperty(sortProperty.split(RegistryHistoryConstants.REVISION_PROPERTY_PREFIX)[1]);
        }
        else if (RegistryHistoryConstants.REVISION_NUMBER_PROPERTY.equals(sortProperty)) {
            auditProperty = AuditEntity.revisionNumber();
        }
        else if (RegistryHistoryConstants.REVISION_TYPE_PROPERTY.equals(sortProperty)) {
            auditProperty = AuditEntity.revisionType();
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
}
