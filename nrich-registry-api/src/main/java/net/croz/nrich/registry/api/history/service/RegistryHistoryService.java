package net.croz.nrich.registry.api.history.service;

import net.croz.nrich.registry.api.history.model.EntityWithRevision;
import net.croz.nrich.registry.api.history.request.ListRegistryHistoryRequest;
import org.springframework.data.domain.Page;

public interface RegistryHistoryService {

    <T> Page<EntityWithRevision<T>> historyList(ListRegistryHistoryRequest request);

}
