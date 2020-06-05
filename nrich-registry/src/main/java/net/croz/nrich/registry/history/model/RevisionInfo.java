package net.croz.nrich.registry.history.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class RevisionInfo {

    private final Long revisionNumber;

    private final Instant revisionDate;

    private final String revisionTypeName;

    private final Map<String, Object> additionalRevisionPropertyMap;

}
