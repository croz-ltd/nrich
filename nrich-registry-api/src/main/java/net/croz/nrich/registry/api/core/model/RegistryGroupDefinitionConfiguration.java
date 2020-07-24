package net.croz.nrich.registry.api.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Configuration for a registry group. Defines group id and a list of entities that are included in the group.
 */
@Setter
@Getter
public class RegistryGroupDefinitionConfiguration {

    /**
     * Unique id of group
     */
    private String groupId;

    /**
     * List of regex patterns that matches included registry entities.
     */
    private List<String> includeEntityPatternList;

    /**
     * List of regex patterns that matches excluded registry entities.
     */
    private List<String> excludeEntityPatternList;

}
