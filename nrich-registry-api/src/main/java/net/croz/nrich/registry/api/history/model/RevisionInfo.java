package net.croz.nrich.registry.api.history.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Entity revision information.
 */
@RequiredArgsConstructor
@Getter
public class RevisionInfo {

    /**
     * Number of revision.
     */
    private final Long revisionNumber;

    /**
     * Revision timestamp.
     */
    private final Instant revisionTimestamp;

    /**
     * Revision type (ADD, MOD, DEL)
     */
    private final String revisionType;

    /**
     * Additional revision properties.
     */
    private final Map<String, Object> additionalRevisionPropertyMap;

}
