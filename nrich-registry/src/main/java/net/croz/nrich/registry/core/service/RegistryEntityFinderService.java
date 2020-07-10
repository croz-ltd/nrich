package net.croz.nrich.registry.core.service;

public interface RegistryEntityFinderService {

    <T> T findEntityInstance(Class<T> type, Object id);

}
