package net.croz.nrich.registry.history.service;

import net.croz.nrich.registry.history.model.EntityWithRevision;
import net.croz.nrich.registry.history.request.ListRegistryHistoryRequest;
import org.springframework.data.domain.Page;

public interface RegistryHistoryService {

    <T> Page<EntityWithRevision<T>> historyList(ListRegistryHistoryRequest request);

}
