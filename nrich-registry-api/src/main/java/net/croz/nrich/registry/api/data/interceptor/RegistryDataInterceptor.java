package net.croz.nrich.registry.api.data.interceptor;

import net.croz.nrich.registry.api.data.request.ListRegistryRequest;

/**
 * Intercepts operations on registry entities.
 */
public interface RegistryDataInterceptor {

    /**
     * Executed before registry list operation.
     *
     * @param request {@link ListRegistryRequest} instance
     */
    void beforeRegistryList(ListRegistryRequest request);

    /**
     * Executed before registry create operation.
     *
     * @param classFullName Class name of registry entity.
     * @param entityData entity creation data
     */
    void beforeRegistryCreate(String classFullName, Object entityData);

    /**
     * Executed before registry update operation.
     *
     * @param classFullName Class name of registry entity.
     * @param id registry entity id.
     * @param entityData entity creation data
     */
    void beforeRegistryUpdate(String classFullName, Object id, Object entityData);

    /**
     * Executed before registry delete operation.
     *
     * @param classFullName Class name of registry entity.
     * @param id registry entity id.
     */
    void beforeRegistryDelete(String classFullName, Object id);

}
