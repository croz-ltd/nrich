package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryServiceRequest;
import org.springframework.data.domain.Page;

public interface RegistryDataService {

    <P> Page<P> list(ListRegistryRequest request);

    <T> T create(CreateRegistryServiceRequest request);

    <T> T update(UpdateRegistryServiceRequest request);

    boolean delete(DeleteRegistryRequest request);

}
