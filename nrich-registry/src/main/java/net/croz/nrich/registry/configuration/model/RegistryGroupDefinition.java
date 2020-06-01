package net.croz.nrich.registry.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RegistryGroupDefinition {

    private final String registryGroupId;

    private final List<String> includeEntityPatternList;

    private final List<String> excludeEntityPatternList;

}
