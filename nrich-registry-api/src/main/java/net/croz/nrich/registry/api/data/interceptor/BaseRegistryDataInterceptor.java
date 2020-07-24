package net.croz.nrich.registry.api.data.interceptor;

import net.croz.nrich.registry.api.data.request.ListRegistryRequest;

public abstract class BaseRegistryDataInterceptor implements RegistryDataInterceptor {

    @Override
    public void beforeRegistryList(final ListRegistryRequest request) {

    }

    @Override
    public void beforeRegistryCreate(final String classFullName, final Object entityData) {

    }

    @Override
    public void beforeRegistryUpdate(final String classFullName, final Object id, final Object entityData) {

    }

    @Override
    public void beforeRegistryDelete(final String classFullName, final Object id) {

    }
}
