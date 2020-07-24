package net.croz.nrich.registry.api.core.model;

import lombok.Builder;
import lombok.Getter;
import net.croz.nrich.search.api.model.SearchConfiguration;

import java.util.Map;

@Getter
@Builder
public class RegistryOverrideConfigurationHolder {

    private final Class<?> type;

    private final RegistryOverrideConfiguration overrideConfiguration;

    private final SearchConfiguration<Object, Object, Map<String, Object>> overrideSearchConfiguration;

}
