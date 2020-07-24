package net.croz.nrich.registry.api.data.interceptor;

import net.croz.nrich.registry.api.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.api.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.api.data.request.UpdateRegistryServiceRequest;

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
     * @param request {@link CreateRegistryServiceRequest} instance
     */
    void beforeRegistryCreate(CreateRegistryServiceRequest request);

    /**
     * Executed before registry update operation.
     *
     * @param request {@link UpdateRegistryServiceRequest} instance
     */
    void beforeRegistryUpdate(UpdateRegistryServiceRequest request);

    /**
     * Executed before registry delete operation.
     *
     * @param request {@link DeleteRegistryRequest} instance
     */
    void beforeRegistryDelete(DeleteRegistryRequest request);

}
