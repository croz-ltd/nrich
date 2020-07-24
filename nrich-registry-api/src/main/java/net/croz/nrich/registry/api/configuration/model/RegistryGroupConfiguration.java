package net.croz.nrich.registry.api.configuration.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Configuration for registry group (a group of registry entities).
 */
@RequiredArgsConstructor
@Getter
public class RegistryGroupConfiguration {

    /**
     * Unique id of group.
     */
    private final String groupId;

    /**
     * Display label for group.
     */
    private final String groupIdDisplayName;

    /**
     * List of entity configurations belonging to this group.
     */
    private final List<RegistryEntityConfiguration> entityConfigurationList;

}
