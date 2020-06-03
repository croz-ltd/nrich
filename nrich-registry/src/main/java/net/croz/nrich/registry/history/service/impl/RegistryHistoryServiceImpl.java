package net.croz.nrich.registry.history.service.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.history.model.EntityWithRevision;
import net.croz.nrich.registry.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.history.service.RegistryHistoryService;
import net.croz.nrich.search.util.PageableUtil;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RegistryHistoryServiceImpl implements RegistryHistoryService {

    private final EntityManager entityManager;

    private final RegistryDataConfigurationHolder registryDataConfigurationHolder;

    @Transactional(readOnly = true)
    public <T> Page<EntityWithRevision<T>> historyList(final ListRegistryHistoryRequest request) {

        final AuditQuery auditQuery = createAuditQuery(request);

        final Pageable pageable = PageableUtil.convertToPageable(request.getPageNumber(), request.getPageSize(), request.getSortPropertyList());

        final List<?> resultList = auditQuery
                .setFirstResult(request.getPageNumber())
                .setMaxResults(request.getPageSize()).getResultList();

        final List<EntityWithRevision<T>> entityWithRevisionList = convertToEntityRevisionList(resultList);

        return PageableExecutionUtils.getPage(entityWithRevisionList, pageable, () -> executeCountQuery(createAuditQuery(request)));
    }

    private <T> AuditQuery createAuditQuery(final ListRegistryHistoryRequest request) {
        @SuppressWarnings("unchecked")
        final Class<T> type = (Class<T>) registryDataConfigurationHolder.findRegistryConfigurationForClass(request.getRegistryId()).getRegistryType();

        final AuditQuery auditQuery = AuditReaderFactory.get(entityManager).createQuery().forRevisionsOfEntity(type, false, true);

        if (request.getRegistryRecordId() != null) {
            auditQuery.add(AuditEntity.id().eq(request.getRegistryRecordId()));
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
                .map(value -> new EntityWithRevision<T>((T) value[0], null))
                .collect(Collectors.toList());
    }
}
