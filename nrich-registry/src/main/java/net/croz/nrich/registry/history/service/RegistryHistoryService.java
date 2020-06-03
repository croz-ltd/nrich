package net.croz.nrich.registry.history.service;

import net.croz.nrich.registry.history.model.EntityWithRevision;
import net.croz.nrich.registry.history.request.ListRegistryHistoryRequest;

import java.util.List;

public interface RegistryHistoryService {

    <T> List<EntityWithRevision<T>> historyList(ListRegistryHistoryRequest request);

}
