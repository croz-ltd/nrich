package net.croz.nrich.registry.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RegistryGroupDefinitionConfiguration {

    private String registryGroupId;

    private List<String> includeEntityPatternList;

    private List<String> excludeEntityPatternList;

}
