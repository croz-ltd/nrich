package net.croz.nrich.registry.api.data.service;

import net.croz.nrich.registry.api.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * Lists, creates, updates and deletes registry entities.
 */
public interface RegistryDataService {

    /**
     * Return a map holding multiple registry entities. Key is registry entity class name and value is {@link Page} of registry entities.
     *
     * @param request {@link ListBulkRegistryRequest} instance holding query information.
     * @return map holding multiple registry entities
     */
    Map<String, Page<?>> listBulk(ListBulkRegistryRequest request);

    /**
     * Returns Springs {@link Page} instance holding found registry instances.
     *
     * @param request {@link ListRegistryRequest} instance holding query information
     * @param <P>     registry query return value (if no override is specified this is registry type, but can be a projection instance)
     * @return {@link Page} instance holding found registry instances
     */
    <P> Page<P> list(ListRegistryRequest request);

    /**
     * Returns created registry entity.
     *
     * @param classFullName Class name of registry entity.
     * @param entityData    entity creation data
     * @param <T>           registry entity type
     * @return created registry instance.
     */
    <T> T create(String classFullName, Object entityData);

    /**
     * Returns update registry entity.
     *
     * @param classFullName Class name of registry entity.
     * @param id            registry entity id.
     * @param entityData    entity creation data
     * @param <T>           registry entity type
     * @return updated registry instance.
     */
    <T> T update(String classFullName, Object id, Object entityData);

    /**
     * Returns deleted registry entity.
     *
     * @param classFullName Class name of registry entity.
     * @param id            registry entity id.
     * @param <T>           registry entity type
     * @return deleted registry instance.
     */
    <T> T delete(String classFullName, Object id);

}
