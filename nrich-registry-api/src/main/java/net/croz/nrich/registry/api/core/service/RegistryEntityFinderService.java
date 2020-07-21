package net.croz.nrich.registry.api.core.service;

import java.util.Map;

public interface RegistryEntityFinderService {

    <T> T findEntityInstance(Class<T> type, Object id);

    <T> Map<String, Object> resolveIdParameterMap(Class<T> type, Object id);

}
