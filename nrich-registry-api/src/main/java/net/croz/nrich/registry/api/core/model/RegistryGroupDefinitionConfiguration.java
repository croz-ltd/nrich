package net.croz.nrich.registry.api.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RegistryGroupDefinitionConfiguration {

    private String groupId;

    private List<String> includeEntityPatternList;

    private List<String> excludeEntityPatternList;

}
