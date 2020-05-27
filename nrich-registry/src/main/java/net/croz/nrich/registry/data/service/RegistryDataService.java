package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import org.springframework.data.domain.Page;

public interface RegistryDataService {

    <P> Page<P> registryList(ListRegistryRequest request);

    boolean registryDelete(DeleteRegistryRequest request);

}
