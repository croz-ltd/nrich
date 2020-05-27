package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryServiceRequest;
import org.springframework.data.domain.Page;

import javax.validation.Valid;

public interface RegistryDataService {

    <P> Page<P> registryList(@Valid ListRegistryRequest request);

    <T> T registryCreate(@Valid CreateRegistryServiceRequest request);

    <T> T registryUpdate(@Valid UpdateRegistryServiceRequest request);

    boolean registryDelete(@Valid DeleteRegistryRequest request);

}
