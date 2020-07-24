package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;

public interface RegistryDataRequestConversionService {

    Object convertEntityDataToTyped(CreateRegistryRequest request);

    Object convertEntityDataToTyped(UpdateRegistryRequest request);

}
