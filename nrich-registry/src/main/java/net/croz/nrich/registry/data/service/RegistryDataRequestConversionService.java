package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;

public interface RegistryDataRequestConversionService {

    CreateRegistryServiceRequest convertToServiceRequest(CreateRegistryRequest request);

}
