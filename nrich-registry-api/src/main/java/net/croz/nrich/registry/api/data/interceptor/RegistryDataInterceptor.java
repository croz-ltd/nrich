package net.croz.nrich.registry.api.data.interceptor;

import net.croz.nrich.registry.api.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.api.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.api.data.request.UpdateRegistryServiceRequest;

public interface RegistryDataInterceptor {

    void beforeRegistryList(ListRegistryRequest request);

    void beforeRegistryCreate(CreateRegistryServiceRequest request);

    void beforeRegistryUpdate(UpdateRegistryServiceRequest request);

    void beforeRegistryDelete(DeleteRegistryRequest request);

}
