package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.api.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.api.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.api.data.request.UpdateRegistryServiceRequest;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface RegistryDataService {

    <P> Map<String, Page<P>> listBulk(ListBulkRegistryRequest request);

    <P> Page<P> list(ListRegistryRequest request);

    <T> T create(CreateRegistryServiceRequest request);

    <T> T update(UpdateRegistryServiceRequest request);

    <T> T delete(DeleteRegistryRequest request);

}
