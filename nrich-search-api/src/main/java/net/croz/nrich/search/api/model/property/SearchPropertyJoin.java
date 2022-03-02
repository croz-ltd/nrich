package net.croz.nrich.search.api.model.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a join between two entities. If entities are to be joined by another property (different from id). Then it needs to be specified.
 */
@RequiredArgsConstructor
@Getter
public class SearchPropertyJoin {

    /**
     * Parent (root class) property name.
     */
    private final String parentProperty;

    /**
     * Child property name.
     */
    private final String childProperty;

    public static SearchPropertyJoin defaultJoinById() {
        return new SearchPropertyJoin("id", "id");
    }
}
