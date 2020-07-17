package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.api.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;
import net.croz.nrich.registry.api.data.request.UpdateRegistryServiceRequest;

public interface RegistryDataRequestConversionService {

    CreateRegistryServiceRequest convertToServiceRequest(CreateRegistryRequest request);

    UpdateRegistryServiceRequest convertToServiceRequest(UpdateRegistryRequest request);

}
