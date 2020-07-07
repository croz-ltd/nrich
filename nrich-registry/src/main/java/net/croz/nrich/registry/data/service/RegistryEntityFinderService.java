package net.croz.nrich.registry.data.service;

public interface RegistryEntityFinderService {

    <T> T findEntityInstance(Class<T> type, Object id);

}
