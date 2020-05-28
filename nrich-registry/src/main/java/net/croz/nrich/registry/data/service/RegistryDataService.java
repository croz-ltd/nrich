package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryServiceRequest;
import org.springframework.data.domain.Page;

public interface RegistryDataService {

    <P> Page<P> registryList(ListRegistryRequest request);

    <T> T registryCreate(CreateRegistryServiceRequest request);

    <T> T registryUpdate(UpdateRegistryServiceRequest request);

    boolean registryDelete(DeleteRegistryRequest request);

}
