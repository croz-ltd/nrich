package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.data.request.RegistryListRequest;
import org.springframework.data.domain.Page;

public interface RegistryDataService {

    <P> Page<P> registryList(RegistryListRequest request);
}
