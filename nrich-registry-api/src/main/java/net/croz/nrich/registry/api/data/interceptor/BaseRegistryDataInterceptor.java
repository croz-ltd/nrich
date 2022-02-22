package net.croz.nrich.registry.api.data.interceptor;

import net.croz.nrich.registry.api.data.request.ListRegistryRequest;

public abstract class BaseRegistryDataInterceptor implements RegistryDataInterceptor {

    @Override
    public void beforeRegistryList(ListRegistryRequest request) {

    }

    @Override
    public void beforeRegistryCreate(String classFullName, Object entityData) {

    }

    @Override
    public void beforeRegistryUpdate(String classFullName, Object id, Object entityData) {

    }

    @Override
    public void beforeRegistryDelete(String classFullName, Object id) {

    }
}
