package net.croz.nrich.registry.history.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;

@Getter
@RequiredArgsConstructor
public class RevisionInfo {

    private final DefaultRevisionEntity revisionEntity;

    private final RevisionType revisionType;

}
