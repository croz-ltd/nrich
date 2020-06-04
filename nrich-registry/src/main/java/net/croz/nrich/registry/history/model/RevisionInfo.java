package net.croz.nrich.registry.history.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class RevisionInfo {

    private final Integer revisionNumber;

    private final Instant revisionDate;

    private final String revisionTypeName;

}
