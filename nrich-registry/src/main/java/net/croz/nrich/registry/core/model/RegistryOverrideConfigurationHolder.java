package net.croz.nrich.registry.core.model;

import lombok.Builder;
import lombok.Getter;
import net.croz.nrich.search.model.SearchConfiguration;

import java.util.Map;

@Getter
@Builder
public class RegistryOverrideConfigurationHolder {

    private final Class<?> type;

    private final RegistryOverrideConfiguration registryOverrideConfiguration;

    private final SearchConfiguration<?, ?, Map<String, Object>> registryDataOverrideSearchConfiguration;

}
