package net.croz.nrich.registry.api.history.service;

import net.croz.nrich.registry.api.history.model.EntityWithRevision;
import net.croz.nrich.registry.api.history.request.ListRegistryHistoryRequest;
import org.springframework.data.domain.Page;

/**
 * Fetches history for specific registry entity.
 */
public interface RegistryHistoryService {

    /**
     * Returns Springs {@link Page} instance holding found {@link EntityWithRevision} instances.
     *
     * @param request {@link ListRegistryHistoryRequest} instance holding query information
     * @param <T>     type of registry entity
     * @return {@link Page} instance holding found {@link EntityWithRevision} instances.
     */
    <T> Page<EntityWithRevision<T>> historyList(ListRegistryHistoryRequest request);

}
