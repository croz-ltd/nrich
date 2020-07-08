package net.croz.nrich.registry.history.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class RevisionInfo {

    private final Long revisionNumber;

    private final Instant revisionTimestamp;

    private final String revisionType;

    private final Map<String, Object> additionalRevisionPropertyMap;

}
